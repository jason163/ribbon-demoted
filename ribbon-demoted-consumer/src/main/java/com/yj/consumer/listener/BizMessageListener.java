package com.yj.consumer.listener;

import com.yj.amqp.listener.AbstractMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/12
 * Time: 15:55
 */
@Component
public class BizMessageListener extends AbstractMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(BizMessageListener.class);
    /**
     * 接收消息，子类必须实现该方法
     *
     * @param msg              消息对象
     * @param messageConverter 消息转换器
     */
    @Override
    public void receiveMessage(Message msg, MessageConverter messageConverter) {

        Object bizObj = messageConverter.fromMessage(msg);

        logger.info("get message success: " + bizObj.toString());

        throw new RuntimeException("it's runtime exception from biz msg listener");

    }
}
