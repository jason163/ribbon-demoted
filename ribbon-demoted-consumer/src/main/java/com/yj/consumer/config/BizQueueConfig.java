package com.yj.consumer.config;

import com.yj.amqp.util.MQConstants;
import com.yj.consumer.listener.BizMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/12
 * Time: 16:02
 */
@Configuration
public class BizQueueConfig {

    /**
     * 声明使用哪个交换机
     * @return
     */
    @Bean
    public DirectExchange businessExchange(){
        return new DirectExchange(MQConstants.BUSINESS_EXCHANGE);
    }

    @Bean
    public Queue bizQueue(){
        Map<String,Object> args =new HashMap<>();
        /**
         * 配置死信队列
         */
        args.put("x-dead-letter-exchange",MQConstants.DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key",MQConstants.DLX_ROUTING_KEY);
        /**
         * 消息被确认前的最大等待时间，默认无限大
         */
        args.put("x-message-ttl",6000);
        /**
         * 消息队列的最大长度，默认永不过期
         */
        args.put("x-max-length",300);

        return new Queue(MQConstants.BUSINESS_QUEUE,true,false,false,args);
    }

    /**
     * 绑定bizQueue到相应Exchange&Key
     * @return
     */
    @Bean
    public Binding bizBinding(){
        return BindingBuilder.bind(bizQueue()).to(businessExchange())
                .with(MQConstants.BUSINESS_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer bizListenerContainer(ConnectionFactory connectionFactory,
                                                               BizMessageListener bizMessageListener){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(bizQueue());
        container.setExposeListenerChannel(true);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(bizMessageListener);
        /**
         * 设置消费者能处理未应答消息的最大个数
         */
        container.setPrefetchCount(10);

        return container;

    }
}
