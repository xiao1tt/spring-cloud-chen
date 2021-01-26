package com.base.commons.mq;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Map;
import org.apache.rocketmq.common.message.Message;

/**
 * @author chenxiaotong
 */
public class RMessage implements Serializable {

    public static final String DEFAULT_BODY_KEY = "message_body";

    private final String topic;
    private String body;
    private final Map<String, String> valueMap = Maps.newHashMap();

    public RMessage(String topic) {
        this.topic = topic;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void put(String key, String value) {
        this.valueMap.put(key, value);
    }

    public void putAll(Map<String, String> map) {
        if (map == null) {
            return;
        }

        this.valueMap.putAll(map);
    }

    public String get(String key) {
        return this.valueMap.get(key);
    }

    public Message toMessage() {
        valueMap.put(DEFAULT_BODY_KEY, body);
        return new Message(topic, JSON.toJSONBytes(valueMap));
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
