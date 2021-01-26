package com.toms.order.center.mq;

import com.base.commons.web.monitor.Monitor;
import com.base.commons.web.mq.MessageProducer;
import com.base.commons.web.mq.RMessage;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author chenxiaotong
 */
@Service
public class SendService {

    @Resource
    private MessageProducer messageProducer;

    @Monitor("send_msg")
    public void send2(String body) {
        RMessage message = new RMessage("test-topic1");
        message.setBody(body);

        messageProducer.send(message);
    }
}
