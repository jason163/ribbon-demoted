package com.yj.amqp.config;

import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.yj.amqp.sender.RabbitSender;
import com.yj.amqp.util.MQConstants;
import com.yj.amqp.util.RabbitMetaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/11
 * Time: 18:14
 */
@Configuration
public class RabbitTemplateConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitTemplateConfig.class);

    private static Boolean SUCESS_SEND = false;

    @Autowired
    RabbitSender rabbitSender;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Bean
    public RabbitTemplate customRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        // mandatory 必须设置为true,RetureCallback才会调用
        /*
        如果exchange根据自身类型和消息routeKey无法找到一个符合条件的queue，那么会调用basic.return方法将消息返还给生产者。false：出现上述情形broker会直接将消息扔掉
        * */
        rabbitTemplate.setMandatory(true);

        // 消息发送到RabbitMQ交换器后接收ACK回调
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause) ->{
            logger.debug("confirm回调,ack={} correlationData ={} cause={}",ack,correlationData,cause);
            String cacheKey = correlationData.getId();
            RabbitMetaMessage metaMessage = (RabbitMetaMessage) redisTemplate.opsForHash().get(MQConstants.MQ_PRODUCER_RETRY_KEY,cacheKey);
            // 只要消息能投入正确的交换器中，并持久化就会返回ack
            if(ack){
                logger.info("消息已正确投递到队列,correlationData:{}",correlationData);
                // 消息已经正确投递到队列，清除重发缓存
                redisTemplate.opsForHash().delete(MQConstants.MQ_PRODUCER_RETRY_KEY,cacheKey);
                SUCESS_SEND = true;
            }else {
                // 无Exchange，以及网络中断的其它异常；重发消息
                logger.error("消息投递到交换机失败,重发消息。ack:{};correlationData:{}; cause:{}",ack,correlationData,cause);
                // 重发消息
                reSendMsg(cacheKey,metaMessage);
            }
        });
        // 消息发送到RabbitMQ交换器，但无相应Queue时触发此回调：重发消息
        rabbitTemplate.setReturnCallback((msg,replyCode,replyText,exchange,routingKey) ->{
            String cacheKey = msg.getMessageProperties().getMessageId();

            logger.error("消息投递至交换机失败，没有找到任何匹配的队列！message id:{},replyCode{},replyText:{},"
                    + "exchange:{},routingKey{}", cacheKey, replyCode, replyText, exchange, routingKey);

            RabbitMetaMessage rabbitMetaMessage = (RabbitMetaMessage)redisTemplate.opsForHash().get(MQConstants.MQ_PRODUCER_RETRY_KEY,cacheKey);

            //重发消息
            reSendMsg(cacheKey,rabbitMetaMessage);
        });

        return rabbitTemplate;
    }

    public void reSendMsg(String msgId,RabbitMetaMessage rabbitMetaMessage){


        /**
         * 内部类
         */
        class ReSendThread implements Callable{

            String msgId;
            RabbitMetaMessage rabbitMetaMessage;

            public ReSendThread(String msgId,RabbitMetaMessage rabbitMetaMessage){
                this.msgId = msgId;
                this.rabbitMetaMessage = rabbitMetaMessage;
            }
            /**
             * Computes a result, or throws an exception if unable to do so.
             *
             * @return computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            public Boolean call() throws Exception {
                // 如果发送成功，重发结束
                if(SUCESS_SEND){
                    return true;
                }
                logger.info("ReSendThread"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                // 重发消息
                rabbitSender.sendMessage(this.rabbitMetaMessage,this.msgId);
                return true;
            }
        }

        // 重发机制
        Retryer<Boolean> retryer = RetryerBuilder
                .<Boolean>newBuilder()
                .retryIfException() // 设置异常重试源
                .retryIfRuntimeException() // 设置异常重试源
                .retryIfResult(Predicates.equalTo(false))
                .withWaitStrategy(WaitStrategies.exponentialWait(MQConstants.MUTIPLIER_TIME,
                        MQConstants.MAX_RETRY_TIME, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.neverStop())
                .build();

        ReSendThread reSendThread = new ReSendThread(msgId,rabbitMetaMessage);


        try {
            retryer.call(reSendThread);
            logger.info("ReSendThread"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            if(!SUCESS_SEND){
                rabbitSender.sendMsgToDeadQueue((String)rabbitMetaMessage.getPayload());
            }
        }catch (Exception ex){
            logger.error("重发消息异常");
        }
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        return jsonMessageConverter;
    }
}
