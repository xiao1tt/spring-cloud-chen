package com.toms.order.center.mq;

import com.base.commons.web.mq.RMessage;
import com.base.commons.web.mq.RSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author chenxiaotong
 */
@Service
public class ReceiveService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RSubscribe(topic = "test-topic1", group = "test-group")
    public void msg1(RMessage message) {
        logger.info("message, group:{}, body:{}", "test-group", message.getBody());
    }

    @RSubscribe(topic = "test-topic1")
    public void msg2(RMessage message) {
        logger.info("message, group:{}, body:{}", "default", message.getBody());
    }
}
