package com.base.commons.mq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author chenxiaotong
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RSubscribe {

    String topic();

    String group() default MessageConstants.DEFAULT_CONSUMER_GROUP;

    MessageModel model() default MessageModel.CLUSTERING;

    OrderMode orderMode() default OrderMode.CONCURRENTLY;

}
