package com.base.commons.mq;

import com.alibaba.fastjson.JSON;
import com.base.commons.mq.trace.TraceSendMessageHook;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenxiaotong
 */
public class MessageProducer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String address;

    public String getAddress() {
        return address;
    }

    public MessageProducer(String address) {
        this.address = address;
    }

    private DefaultMQProducer delegateProducer;

    public DefaultMQProducer getDelegateProducer() {
        return delegateProducer;
    }

    public void start() {
        if (StringUtils.isBlank(address)) {
            throw new IllegalArgumentException("mq address can not be empty");
        }

        if (delegateProducer != null) {
            delegateProducer.shutdown();
        }

        delegateProducer = new DefaultMQProducer(MessageConstants.DEFAULT_PRODUCER_GROUP);
        delegateProducer.setNamesrvAddr(address);
        try {
            registerHook(delegateProducer);
            delegateProducer.start();
        } catch (Exception e) {
            throw new RuntimeException("mq producer start error, address:" + address, e);
        }
    }

    private void registerHook(DefaultMQProducer delegateProducer) {
        delegateProducer.getDefaultMQProducerImpl().registerSendMessageHook(new TraceSendMessageHook());
    }

    public void shutdown() {
        delegateProducer.shutdown();
    }

    public void send(RMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message can not be null");
        }

        try {
            delegateProducer.send(message.toMessage());
        } catch (Exception e) {
            throw new RuntimeException("message send error. address:" + address + ", msg:" + JSON.toJSONString(message),
                    e);
        }
    }

    public void send(Collection<RMessage> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }

        List<Message> messageList = new ArrayList<>();

        for (RMessage rMessage : messages) {
            if (rMessage == null) {
                logger.warn("mq producer batch send warning : exist null message， message collection:{}", messages);
                continue;
            }

            messageList.add(rMessage.toMessage());
        }

        try {
            delegateProducer.send(messageList);
        } catch (Exception e) {
            throw new RuntimeException(
                    "message batch send error. address:" + address + ", msg:" + JSON.toJSONString(messages), e);
        }
    }
}
