package com.yj.amqp.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.amqp.util.MQConstants;
import com.yj.amqp.util.RabbitMetaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 封装 Rabbit MQ 消息发送
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/11
 * Time: 19:29
 */
@Component
public class RabbitSender {

    private static final Logger logger = LoggerFactory.getLogger(RabbitSender.class);

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public String send(RabbitMetaMessage rabbitMetaMessage) throws Exception{
        final String msgId = UUID.randomUUID().toString();

        redisTemplate.opsForHash().put(MQConstants.MQ_PRODUCER_RETRY_KEY,msgId,rabbitMetaMessage);

        return sendMessage(rabbitMetaMessage,msgId);
    }

    public String sendMessage(RabbitMetaMessage rabbitMetaMessage, final String msgId) throws Exception{
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setMessageId(msgId);
                // 设置消息持久化
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }
        };

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(rabbitMetaMessage.getPayload());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(),messageProperties);
        try {
            rabbitTemplate.convertAndSend(rabbitMetaMessage.getExchange(),rabbitMetaMessage.getRoutingKey(),
                    message,messagePostProcessor,new CorrelationData(msgId));
            logger.info("发送信息，消息ID：{}",msgId);
            return msgId;
        }catch (AmqpException ex){
            logger.error("Rabbit MQ发送信息失败");
            throw new RuntimeException("Rabbit MQ发送信息失败");
        }
    }

    /**
     * 发送消息到死信队列
     * @param msg
     * @return
     * @throws Exception
     */
    public String sendMsgToDeadQueue(String msg) throws Exception{
        RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();

        // 设置交换机
        rabbitMetaMessage.setExchange(MQConstants.DLX_EXCHANGE);
        // 设置路由关键字
        rabbitMetaMessage.setRoutingKey(MQConstants.DLX_ROUTING_KEY);
        // 设置消息内容
        rabbitMetaMessage.setPayload(msg);

        return send(rabbitMetaMessage);
    }
}
