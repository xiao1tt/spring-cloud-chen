package com.base.commons.mq.trace;

import java.util.List;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

/**
 * @author chenxiaotong
 */
public class TraceHandler {

    public final void beforeMethod(List<MessageExt> msgs) {
        ContextCarrier contextCarrier = this.getContextCarrierFromMessage(msgs.get(0));
        AbstractSpan span = ContextManager
                .createEntrySpan("RocketMQ/" + msgs.get(0).getTopic() + "/Consumer", contextCarrier);
        span.setComponent(ComponentsDefine.ROCKET_MQ_CONSUMER);
        SpanLayer.asMQ(span);

        for (int i = 1; i < msgs.size(); ++i) {
            ContextManager.extract(this.getContextCarrierFromMessage(msgs.get(i)));
        }
    }

    public Object afterMethod(String ret) {
        if (ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT.name().equals(ret)
                || ConsumeConcurrentlyStatus.RECONSUME_LATER.name().equals(ret)) {
            AbstractSpan activeSpan = ContextManager.activeSpan();
            activeSpan.errorOccurred();
            Tags.STATUS_CODE.set(activeSpan, ret);
        }
        ContextManager.stopSpan();
        return ret;
    }

    public final void handleMethodException(Throwable t) {
        ContextManager.activeSpan().log(t);
    }

    private ContextCarrier getContextCarrierFromMessage(MessageExt message) {
        ContextCarrier contextCarrier = new ContextCarrier();
        CarrierItem next = contextCarrier.items();

        while (next.hasNext()) {
            next = next.next();
            next.setHeadValue(message.getUserProperty(next.getHeadKey()));
        }

        return contextCarrier;
    }
}
