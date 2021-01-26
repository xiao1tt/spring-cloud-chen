package com.base.commons.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.base.commons.mq.trace.TraceConsumeMessageHook;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author chenxiaotong
 */
public class MessageSubscriberRegistry implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final Map<String, MQConsumer> consumerMap = new ConcurrentHashMap<>();

    public void register(String topic, MQConsumer consumer) {
        consumerMap.put(topic, consumer);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            RSubscribe subscribe = AnnotationUtils.findAnnotation(method, RSubscribe.class);
            if (subscribe == null) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1 || !parameterTypes[0].equals(RMessage.class)) {
                throw new IllegalArgumentException(
                        "subscribe method parameter error, must have and only have [RMessage]");
            }

            logger.info("find message subscriber, class:{}, message:{}, topic:{}",
                    beanClass.getSimpleName(), method.getName(), subscribe.topic());

            try {
                register(bean, method, subscribe);
            } catch (Exception e) {
                throw new RuntimeException(
                        "message consumer register failed, bean:" + beanName + ", method:" + method.getName());
            }
        }

        return bean;
    }

    private void register(Object bean, Method method, RSubscribe subscribe) throws MQClientException {
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(groupOf(subscribe));

        pushConsumer.setMessageModel(subscribe.model());
        pushConsumer.subscribe(subscribe.topic(), "*");

        switch (subscribe.orderMode()) {
            case ORDERLY:
                pushConsumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
                    for (MessageExt msg : msgs) {
                        RMessage message = messageOf(msg);
                        try {
                            method.invoke(bean, message);
                        } catch (Exception e) {
                            logger.error("message consume failure, msg:{}", message, e);
                            TraceConsumeMessageHook.TRACE_HANDLER.handleMethodException(e);
                            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                        }
                    }

                    return ConsumeOrderlyStatus.SUCCESS;
                });
                break;
            case CONCURRENTLY:
            default:
                pushConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                    for (MessageExt msg : msgs) {
                        RMessage message = messageOf(msg);
                        try {
                            method.invoke(bean, message);
                        } catch (Exception e) {
                            logger.error("message consume failure, msg:{}", message, e);
                            TraceConsumeMessageHook.TRACE_HANDLER.handleMethodException(e);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                });
        }

        register(subscribe.topic(), pushConsumer);
        pushConsumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(new TraceConsumeMessageHook());
        pushConsumer.start();
    }

    private String groupOf(RSubscribe subscribe) {
        return MessageConstants.DEFAULT_CONSUMER_GROUP.equals(subscribe.group())
                ? subscribe.topic() + "_default_group"
                : subscribe.topic() + "_" + subscribe.group();
    }

    private RMessage messageOf(MessageExt msg) {
        Map<String, String> content = (Map<String, String>) JSON.parse(msg.getBody(), Feature.AllowComment);

        String body = content.get(RMessage.DEFAULT_BODY_KEY);

        RMessage message = new RMessage(msg.getTopic());
        message.setBody(body);
        message.putAll(content);
        return message;
    }
}
