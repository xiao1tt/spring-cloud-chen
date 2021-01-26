package com.base.commons.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenxiaotong
 */
@Configuration
public class MessageComponentRegistry {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.cloud.stream.rocketmq.binder.name-server}")
    private String namesrvAddr;

    @Bean(destroyMethod = "shutdown")
    public MessageProducer messageProducer() {
        logger.info("mq-producer config init.");
        MessageProducer messageProducer = new MessageProducer(namesrvAddr);
        messageProducer.start();
        return messageProducer;
    }
}
