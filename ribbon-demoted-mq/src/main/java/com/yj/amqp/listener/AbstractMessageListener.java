package com.yj.amqp.listener;

import com.rabbitmq.client.Channel;
import com.yj.amqp.util.MQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 消息接收者监听器
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/11
 * Time: 20:25
 */
public abstract class AbstractMessageListener implements ChannelAwareMessageListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Jackson2JsonMessageConverter messageConverter;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 接收消息，子类必须实现该方法
     * @param msg 消息对象
     * @param messageConverter 消息转换器
     */
    public abstract void receiveMessage(Message msg, MessageConverter messageConverter);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        Long deliveryTag = messageProperties.getDeliveryTag();
        Long consumerCount = redisTemplate.opsForHash().increment(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                messageProperties.getMessageId(),1);

        logger.info("当前消息ID:{} 消费次数：{}", messageProperties.getMessageId(), consumerCount);

        try {
            receiveMessage(message,this.messageConverter);
            // 接收成功的回执，第二个参数为false;表示关闭RabbitMQ的自动应答机制，改为手动应答
            channel.basicAck(deliveryTag,false);

        }catch (Exception e){
            logger.error("RabbitMQ 消息消费失败，" + e.getMessage(), e);
            if(consumerCount >= MQConstants.MAX_CONSUMER_COUNT){
                // 入死信队列,
                /**
                 * 消息变成死信有以下向种情况 1：消息被拒绝并且不重回队列；2。消息TTL过期；3。队列达到最大长度
                 */
                channel.basicReject(deliveryTag,false);
            }else {
                Thread.sleep((long) (Math.pow(MQConstants.BASE_NUM, consumerCount)*1000));
                //deliveryTag:消息的index;multiple:是否批量一次性拒绝所有小于deliveryTag的消息；requeue:被拒绝的是否重新入队列
                channel.basicNack(deliveryTag,false,true);
            }
            return;
        }

        try {
            redisTemplate.opsForHash().delete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                    messageProperties.getMessageId());
        }catch (Exception ex){
            logger.error("消息监听redis删除消费异常"+ex);
        }
    }
}
