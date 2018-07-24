package com.yj.amqp.util;

/**
 * 发送消息实体类
 * Create with IDEA
 * User: Jason
 * Date: 2018/6/11
 * Time: 19:32
 */
public class RabbitMetaMessage {

    String exchange;
    String routingKey;
    Boolean autoTrigger;
    Boolean returnCallback;
    Object payload;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public Boolean getAutoTrigger() {
        return autoTrigger;
    }

    public void setAutoTrigger(Boolean autoTrigger) {
        this.autoTrigger = autoTrigger;
    }

    public Boolean getReturnCallback() {
        return returnCallback;
    }

    public void setReturnCallback(Boolean returnCallback) {
        this.returnCallback = returnCallback;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
