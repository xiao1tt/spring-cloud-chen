package com.chen.nozdormu.core.thread;

import com.chen.nozdormu.core.biz.AdminBiz;
import com.chen.nozdormu.core.biz.model.RegistryParam;
import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.enums.RegistryConfig;
import com.chen.nozdormu.core.executor.JobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by xuxueli on 17/3/2.
 */
public class ExecutorRegistryThread {

    private static Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);

    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();

    public static ExecutorRegistryThread getInstance() {
        return instance;
    }

    private Thread registryThread;
    private volatile boolean toStop = false;

    public void start(final String appName, final String address) {

        // valid
        if (appName == null || appName.trim().length() == 0) {
            logger.warn("executor registry config fail, appname is null.");
            return;
        }
        if (JobExecutor.getAdminBizList() == null) {
            logger.warn("executor registry config fail, adminAddresses is null.");
            return;
        }

        registryThread = new Thread(() -> {
            // registry
            while (!toStop) {
                doRun(appName, address);
            }

            // registry remove
            doStop(appName, address);
            logger.info("executor registry thread destory.");
        });

        registryThread.setDaemon(true);
        registryThread.setName("executor ExecutorRegistryThread");
        registryThread.start();
    }

    private void doStop(String appName, String address) {
        try {
            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegisterType.EXECUTOR.name(), appName,
                    address);
            for (AdminBiz adminBiz : JobExecutor.getAdminBizList()) {
                try {
                    ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
                    if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                        registryResult = ReturnT.SUCCESS;
                        logger.info("registry-remove success, registryParam:{}, registryResult:{}", registryParam, registryResult);
                        break;
                    } else {
                        logger.info("registry-remove fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.info("registry-remove error, registryParam:{}", registryParam,
                                e);
                    }

                }

            }
        } catch (Exception e) {
            if (!toStop) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void doRun(String appName, String address) {
        try {
            String jobs = String.join(",", JobExecutor.loadAllJobs());
            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegisterType.EXECUTOR.name(), appName,
                    address, jobs);

            for (AdminBiz adminBiz : JobExecutor.getAdminBizList()) {
                try {
                    ReturnT<String> registryResult = adminBiz.registry(registryParam);
                    if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                        registryResult = ReturnT.SUCCESS;
                        logger.debug("registry success, registryParam:{}, registryResult:{}", registryParam, registryResult);
                        break;
                    } else {
                        logger.info("registry fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
                    }
                } catch (Exception e) {
                    logger.info("registry error, registryParam:{}", registryParam, e);
                }

            }
        } catch (Exception e) {
            if (!toStop) {
                logger.error(e.getMessage(), e);
            }

        }

        try {
            if (!toStop) {
                TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
            }
        } catch (InterruptedException e) {
            if (!toStop) {
                logger.warn(" executor registry thread interrupted, error msg:{}",
                        e.getMessage());
            }
        }
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        registryThread.interrupt();
        try {
            registryThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
