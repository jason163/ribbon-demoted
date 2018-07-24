package com.yj.provider.controller;

import com.yj.amqp.sender.RabbitSender;
import com.yj.amqp.util.MQConstants;
import com.yj.amqp.util.RabbitMetaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/12
 * Time: 13:30
 */
@RestController
@RequestMapping("/mq")
public class MessageController {

    @Value("${spring.rabbitmq.host}")
    private String rabiitmqAddress;

    @Autowired
    RabbitSender rabbitSender;

    @RequestMapping(value = "/config",method = RequestMethod.GET)
    public String testConfig(){
        return rabiitmqAddress;
    }

    @RequestMapping(value = "/send",method = RequestMethod.GET)
    public String sendMsg() throws Exception{
        RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();
        rabbitMetaMessage.setExchange(MQConstants.BUSINESS_EXCHANGE);
        rabbitMetaMessage.setRoutingKey(MQConstants.BUSINESS_KEY);

        rabbitMetaMessage.setPayload("the msg from hello world!");

        rabbitSender.send(rabbitMetaMessage);

        return "OK";
    }
}
