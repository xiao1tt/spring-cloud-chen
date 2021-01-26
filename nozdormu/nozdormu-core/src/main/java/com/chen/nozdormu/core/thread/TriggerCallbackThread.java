package com.chen.nozdormu.core.thread;

import com.chen.nozdormu.core.biz.AdminBiz;
import com.chen.nozdormu.core.biz.model.HandleCallbackParam;
import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.executor.JobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author xuxueli
 * @date 16/7/22
 */
public class TriggerCallbackThread {

    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static TriggerCallbackThread instance = new TriggerCallbackThread();

    public static TriggerCallbackThread getInstance() {
        return instance;
    }

    /**
     * job results callback queue
     */
    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<HandleCallbackParam>();

    public static void pushCallBack(HandleCallbackParam callback) {
        getInstance().callBackQueue.add(callback);
        logger.debug("push callback request, logId:{}", callback.getLogId());
    }

    /**
     * callback thread
     */
    private Thread triggerCallbackThread;
    private volatile boolean toStop = false;

    public void start() {
        // valid
        if (JobExecutor.getAdminBizList() == null) {
            logger.warn(" executor callback config fail, adminAddresses is null.");
            return;
        }

        // callback
        triggerCallbackThread = new Thread(() -> {
            // normal callback
            while (!toStop) {
                try {
                    HandleCallbackParam callback = getInstance().callBackQueue.take();
                    doCallback(callback);
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.info("executor callback thread destory.");
        });

        triggerCallbackThread.setDaemon(true);
        triggerCallbackThread.setName("executor TriggerCallbackThread");
        triggerCallbackThread.start();
    }

    public void toStop() {
        toStop = true;
        // stop callback, interrupt and wait
        // support empty admin address
        if (triggerCallbackThread != null) {
            triggerCallbackThread.interrupt();
            try {
                triggerCallbackThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * do callback, will retry if error
     */
    private void doCallback(HandleCallbackParam callbackParam) {
        if (callbackParam == null) {
            return;
        }

        List<HandleCallbackParam> paramList = new ArrayList<>();
        paramList.add(callbackParam);

        // callback, will retry if error
        for (AdminBiz adminBiz : JobExecutor.getAdminBizList()) {
            try {
                ReturnT<String> callbackResult = adminBiz.callback(paramList);

                if (callbackResult != null && ReturnT.SUCCESS_CODE == callbackResult.getCode()) {
                    logger.info("job callback finish. param:{}", callbackParam);
                    break;
                } else {
                    logger.info("job callback fail, param:{}, callbackResult:{}", callbackParam, callbackResult);
                }
            } catch (Exception e) {
                logger.info("job callback error, param:{}", callbackParam, e);
            }
        }
    }
}
