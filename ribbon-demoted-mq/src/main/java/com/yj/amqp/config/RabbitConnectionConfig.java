package com.yj.amqp.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Rabbit MQ 连接工厂配置
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/11
 * Time: 15:07
 */
@Configuration
public class RabbitConnectionConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitConnectionConfig.class);

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory(RabbitProperties rabbitProperties) throws Exception{

        logger.info("==> custom rabbitmq connection factory,address: {}",rabbitProperties.getAddresses());

        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        if(rabbitProperties.determineHost() != null){
            factory.setHost(rabbitProperties.determineHost());
        }
        factory.setPort(rabbitProperties.determinePort());
        if(rabbitProperties.determineUsername() != null){
            factory.setUsername(rabbitProperties.determineUsername());
        }
        if(rabbitProperties.determinePassword() != null){
            factory.setPassword(rabbitProperties.determinePassword());
        }
        if(rabbitProperties.determineVirtualHost() != null){
            factory.setVirtualHost(rabbitProperties.determineVirtualHost());
        }

        RabbitProperties.Ssl ssl = rabbitProperties.getSsl();
        if(ssl.isEnabled()){
            factory.setUseSSL(true);
            if(ssl.getAlgorithm() != null){
                factory.setSslAlgorithm(ssl.getAlgorithm());
            }
            factory.setKeyStore(ssl.getKeyStore());
            factory.setKeyStorePassphrase(ssl.getKeyStorePassword());
            factory.setTrustStore(ssl.getTrustStore());
            factory.setTrustStorePassphrase(ssl.getTrustStorePassword());
        }

        if(rabbitProperties.getConnectionTimeout() != null){
            factory.setConnectionTimeout(rabbitProperties.getConnectionTimeout());
        }
        factory.afterPropertiesSet();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
        connectionFactory.setAddresses(shuffle(rabbitProperties.determineAddresses()));

        // 设置消息必须confirm
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);

        if(rabbitProperties.getCache().getChannel().getSize() != null){
            connectionFactory.setChannelCacheSize(rabbitProperties.getCache().getChannel().getSize());
        }
        if(rabbitProperties.getCache().getConnection().getMode() != null){
            connectionFactory.setCacheMode(rabbitProperties.getCache().getConnection().getMode());
        }
        if(rabbitProperties.getCache().getConnection().getSize() != null){
            connectionFactory.setConnectionCacheSize(rabbitProperties.getCache().getConnection().getSize());

        }
        if(rabbitProperties.getCache().getChannel().getCheckoutTimeout() != null){
            connectionFactory.setChannelCheckoutTimeout(rabbitProperties.getCache().getChannel().getCheckoutTimeout());
        }

        return  connectionFactory;
    }

    /**
     * 用于启动时，每个节点连接随机的Rabbit MQ服务器
     * @param address Rabbit MQ服务器列表，比如：ip:port,ip:port
     * @return 搅乱后的rabbit mq服务器列表
     */
    private String shuffle(String address){
        String[] addrArray = StringUtils.commaDelimitedListToStringArray(address);
        List<String> addrList = Arrays.asList(addrArray);
        Collections.shuffle(addrList);

        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = addrList.iterator();
        while (iterator.hasNext()){
            stringBuilder.append(iterator.next());
            if(iterator.hasNext()){
                stringBuilder.append(",");
            }
        }
        logger.info("==> rabbitmq shuffle addresses: {}",stringBuilder);

        return  stringBuilder.toString();
    }
}
