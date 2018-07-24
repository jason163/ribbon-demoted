package com.yj.amqp.util;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/11
 * Time: 19:34
 */
public class MQConstants {

    /**
     * 业务交换机名称
     */
    public static final String BUSINESS_EXCHANGE = "business.exchange";

    /**
     * 业务队列名称
     */
    public static final String BUSINESS_QUEUE = "business.queue";
    /**
     * 业务Key
     */
    public static final String BUSINESS_KEY = "business.key";


    public static final String MQ_PRODUCER_RETRY_KEY = "mq.producer.retry.key";
    public static final String MQ_CONSUMER_RETRY_COUNT_KEY = "mq.consumer.retry.count.key";
    /**死信队列配置*/
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLX_QUEUE = "dlx.queue";
    public static final String DLX_ROUTING_KEY = "dlx.routing.key";
    /**发送端重试乘数(ms)*/
    public static final int MUTIPLIER_TIME = 500;
    /** 发送端最大重试时时间（s）*/
    public static final int MAX_RETRY_TIME = 10;
    /** 消费端最大重试次数 */
    public static final int MAX_CONSUMER_COUNT = 5;
    /** 递增时的基本常量 */
    public static final int BASE_NUM = 2;
    /** 空的字符串 */
    public static final String BLANK_STR = "";
}
