package com.chen.nozdormu.admin.core.scheduler;

import com.chen.nozdormu.admin.core.conf.JobAdminConfig;
import com.chen.nozdormu.admin.core.thread.JobFailMonitorHelper;
import com.chen.nozdormu.admin.core.thread.JobLogReportHelper;
import com.chen.nozdormu.admin.core.thread.JobLosedMonitorHelper;
import com.chen.nozdormu.admin.core.thread.JobRegistryMonitorHelper;
import com.chen.nozdormu.admin.core.thread.JobScheduleHelper;
import com.chen.nozdormu.admin.core.thread.JobTriggerPoolHelper;
import com.chen.nozdormu.admin.core.util.I18nUtil;
import com.chen.nozdormu.core.biz.ExecutorBiz;
import com.chen.nozdormu.core.biz.client.ExecutorBizClient;
import com.chen.nozdormu.core.enums.ExecutorBlockStrategyEnum;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2018-10-28 00:18:17
 */

public class JobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);


    public void init() {
        // init i18n
        initI18n();

        // admin registry monitor run
        JobRegistryMonitorHelper.getInstance().start();

        // admin fail-monitor run
        JobFailMonitorHelper.getInstance().start();

        // admin lose-monitor run
        JobLosedMonitorHelper.getInstance().start();

        // admin trigger pool start
        JobTriggerPoolHelper.toStart();

        // admin log report start
        JobLogReportHelper.getInstance().start();

        // start-schedule
        JobScheduleHelper.getInstance().start();

        logger.info("init admin success.");
    }


    public void destroy() throws Exception {

        // stop-schedule
        JobScheduleHelper.getInstance().toStop();

        // admin log report stop
        JobLogReportHelper.getInstance().toStop();

        // admin trigger pool stop
        JobTriggerPoolHelper.toStop();

        // admin lose-monitor stop
        JobLosedMonitorHelper.getInstance().toStop();

        // admin fail-monitor stop
        JobFailMonitorHelper.getInstance().toStop();

        // admin registry stop
        JobRegistryMonitorHelper.getInstance().toStop();

    }

    // ---------------------- I18n ----------------------

    private void initI18n() {
        for (ExecutorBlockStrategyEnum item : ExecutorBlockStrategyEnum.values()) {
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }

    // ---------------------- executor-client ----------------------
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();

    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        // valid
        if (address == null || address.trim().length() == 0) {
            return null;
        }

        // load-cache
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // set-cache
        executorBiz = new ExecutorBizClient(address, JobAdminConfig.getAdminConfig().getAccessToken());

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }

}
