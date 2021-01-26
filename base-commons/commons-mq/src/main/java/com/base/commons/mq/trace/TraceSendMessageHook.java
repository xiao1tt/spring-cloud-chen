package com.base.commons.mq.trace;

import com.base.commons.mq.MessageConstants;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author chenxiaotong
 */
public class TraceSendMessageHook implements SendMessageHook {

    private final Logger logger = LoggerFactory.getLogger("rocketmq");

    @Override
    public String hookName() {
        return "SendMessageLogHook";
    }

    @Override
    public void sendMessageBefore(SendMessageContext context) {
        MDC.put(MessageConstants.MSG_LOG_KEY,
                context.getMessage().getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX));

        logger.info("准备发送消息，topic:{}", context.getMessage().getTopic());
    }

    @Override
    public void sendMessageAfter(SendMessageContext context) {
        SendResult sendResult = context.getSendResult();
        if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
            logger.info("消息发送成功，topic:{}", context.getMessage().getTopic());
        } else {
            logger.error("消息发送失败，topic:{}, reason:{}", context.getMessage().getTopic(), sendResult.getSendStatus());
        }

        MDC.remove(MessageConstants.MSG_LOG_KEY);
    }
}
