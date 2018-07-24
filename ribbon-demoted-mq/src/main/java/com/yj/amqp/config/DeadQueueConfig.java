package com.yj.amqp.config;

import com.rabbitmq.client.Channel;
import com.yj.amqp.util.MQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RabbitMQ交换机、队列的配置类。定义交换机、key、queue并做好绑定
 * 定义每个队列的ttl,队列最大长度,Qos等待
 * 这里只绑定了残念队列。建议每个队列定义自己的QueueConfig
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/11
 * Time: 20:51
 */
@Configuration
public class DeadQueueConfig {

    public class DeadLetterMessageListener implements ChannelAwareMessageListener{
        private final Logger logger = LoggerFactory.getLogger(DeadLetterMessageListener.class);
        @Autowired
        private RedisTemplate<String,Object> redisTemplate;

        @Override
        public void onMessage(Message message, Channel channel) throws Exception {
            MessageProperties messageProperties = message.getMessageProperties();

            String messageBody = new String(message.getBody());
            logger.warn("dead letter message：{} | tag：{}", messageBody, message.getMessageProperties().getDeliveryTag());

            /*// 入库
			insertRecord(logKey, message);
			// 发邮件
			sendEmail(logKey, messageProperties.getMessageId(), messageBody);*/

            // 接收成功的回执，第二个参数为false;表示关闭RabbitMQ的自动应答机制，改为手动应答
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            redisTemplate.opsForHash().delete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY, messageProperties.getMessageId());
        }

        /**
         * 死信交换机
         * @return
         */
        @Bean
        public DirectExchange dlxExchange(){
            return new DirectExchange(MQConstants.DLX_EXCHANGE);
        }

        /**
         * 死信队列
         * @return
         */
        @Bean
        public Queue dlxQueue(){
            return new Queue(MQConstants.DLX_QUEUE,true,false,false);
        }

        /**
         * 通过死信路由key绑定交换机和死信队列
         * @return
         */
        @Bean
        public Binding dlxBinding(){
            return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(MQConstants.DLX_ROUTING_KEY);
        }

        /**
         * 死信队列的监听
         * @param connectionFactory RabbitMQ连接工厂
         * @param deadLetterMessageListener 死信的监听者
         * @return
         */
        @Bean
        public SimpleMessageListenerContainer deadLetterListenerContainer(ConnectionFactory connectionFactory,
                                                                          DeadLetterMessageListener deadLetterMessageListener){

            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
            container.setQueues(dlxQueue());
            container.setExposeListenerChannel(true);
            container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
            container.setMessageListener(deadLetterMessageListener);

            // 设置消费者能处理消息的最大个数
            container.setPrefetchCount(100);

            return  container;
        }

        /**
         * 入库
         *//*
		private void insertRecord(String logKey, Message message) {
			try {
				MessageProperties msgProp = message.getMessageProperties();
				DeadLetterMessageVo entity = new DeadLetterMessageVo();
				entity.setId(Identity.asyncUUID());
				entity.setMsgId(msgProp.getMessageId());
				entity.setMsgBody(new String(message.getBody()));
				entity.setDeliverTag("" + msgProp.getDeliveryTag());
				// PRODUCER为生产,CONSUMER为消费
				entity.setType(StringUtil.isNotBlank(msgProp.getType()) ? msgProp.getType() : Constants.MQ_CONSUMER);
				deadLetterMessageMapper.insert(entity);
				logger.info("{}|死信入库, {}", logKey, entity.toString());
			} catch (Exception e) {
				logger.error("{}|死信入库发生异常,{}", logKey, e.getMessage());
			}
		}

		*//**
         * 发邮件
         *//*
		private void sendEmail(String logKey, String msgId, String msgBody) {
			try {
				String subject = "MQ处理异常";
				StringBuilder cnt = new StringBuilder();
				cnt.append("ID:" + msgId + "\n");
				cnt.append("消息体:" + msgBody + "\n");
				cnt.append("出现异常,请及时处理");
				mailService.sendSimpleMail(emailRecipient, subject, cnt.toString());
				logger.info("{}|发送邮件成功!", logKey);
			} catch (Exception e) {
				logger.error("{}|发送邮件----异常----", logKey, e.getMessage());
			}
		}*/
    }
}
