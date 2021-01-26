package com.chen.nozdormu.core.biz.impl;

import com.chen.nozdormu.core.biz.ExecutorBiz;
import com.chen.nozdormu.core.biz.model.IdleBeatParam;
import com.chen.nozdormu.core.biz.model.KillParam;
import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.biz.model.TriggerParam;
import com.chen.nozdormu.core.enums.ExecutorBlockStrategyEnum;
import com.chen.nozdormu.core.executor.JobExecutor;
import com.chen.nozdormu.core.handler.IJobHandler;
import com.chen.nozdormu.core.thread.JobThread;

/**
 * @author xuxueli
 * @date 17/3/1
 */
public class ExecutorBizImpl implements ExecutorBiz {

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {

        // isRunningOrHasQueue
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = JobExecutor.loadJobThread(idleBeatParam.getJobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "job thread is running or has trigger queue.");
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        // load old：jobHandler + jobThread
        JobThread jobThread = JobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;

        // new jobhandler
        IJobHandler newJobHandler = JobExecutor.loadJobHandler(triggerParam.getExecutorHandler());

        // valid old jobThread
        if (jobThread != null && jobHandler != newJobHandler) {
            // change handler, need kill old thread
            removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";

            jobThread = null;
            jobHandler = null;
        }

        // valid handler
        if (jobHandler == null) {
            jobHandler = newJobHandler;
            if (jobHandler == null) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
            }
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum
                    .match(triggerParam.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return new ReturnT<>(ReturnT.FAIL_CODE,
                            "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = JobExecutor.registerJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue
        return jobThread.pushTriggerQueue(triggerParam);
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        // kill handlerThread, and create new one
        JobThread jobThread = JobExecutor.loadJobThread(killParam.getJobId());
        if (jobThread != null) {
            JobExecutor.removeJobThread(killParam.getJobId(), "scheduling center kill job.");
            return ReturnT.SUCCESS;
        }

        return new ReturnT<>(ReturnT.SUCCESS_CODE, "job thread already killed.");
    }
}
