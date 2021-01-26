package com.base.commons.mq.trace;

import com.base.commons.mq.MessageConstants;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author chenxiaotong
 */
public class TraceConsumeMessageHook implements ConsumeMessageHook {

    public static final TraceHandler TRACE_HANDLER = new TraceHandler();

    private final Logger logger = LoggerFactory.getLogger("rocketmq");

    @Override
    public String hookName() {
        return "ConsumeMessageLogHook";
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        MDC.put(MessageConstants.MSG_LOG_KEY, context.getMsgList().get(0).getMsgId());

        TRACE_HANDLER.beforeMethod(context.getMsgList());

        for (MessageExt messageExt : context.getMsgList()) {
            logger.info("准备消费消息，topic:{}, group:{}", messageExt.getTopic(), context.getConsumerGroup());
        }
    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        for (MessageExt messageExt : context.getMsgList()) {
            logger.info("消息消费完毕，topic:{}, group:{}, success:{}, status:{}", messageExt.getTopic(),
                    context.getConsumerGroup(), context.isSuccess(), context.getStatus());
        }

        TRACE_HANDLER.afterMethod(context.getStatus());
        MDC.remove(MessageConstants.MSG_LOG_KEY);
    }
}
