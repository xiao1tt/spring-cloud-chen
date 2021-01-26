package com.chen.nozdormu.admin.core.thread;

import com.chen.nozdormu.admin.core.conf.JobAdminConfig;
import com.chen.nozdormu.admin.core.model.JobGroup;
import com.chen.nozdormu.admin.core.model.JobRegistry;
import com.chen.nozdormu.admin.dao.JobRegistryDao;
import com.chen.nozdormu.core.enums.RegistryConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * job registry instance
 *
 * @author xuxueli 2016-10-02 19:10:24
 */
public class JobRegistryMonitorHelper {

    private static Logger logger = LoggerFactory.getLogger(JobRegistryMonitorHelper.class);

    private static JobRegistryMonitorHelper instance = new JobRegistryMonitorHelper();

    public static JobRegistryMonitorHelper getInstance() {
        return instance;
    }

    private Thread registryThread;
    private volatile boolean toStop = false;

    public void start() {
        registryThread = new Thread(() -> {
            while (!toStop) {
                try {
                    // auto registry group
                    List<JobGroup> groupList = JobAdminConfig.getAdminConfig().getJobGroupDao()
                            .findByAddressType(0);

                    if (groupList != null && !groupList.isEmpty()) {
                        // remove dead address (admin/executor)
                        clearDead();

                        // fresh online address (admin/executor)
                        refreshOnline(groupList);
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        logger.error(" job registry monitor thread error", e);
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        logger.error(" job registry monitor thread error", e);
                    }
                }
            }
            logger.info("job registry monitor thread stop");
        });

        registryThread.setDaemon(true);
        registryThread.setName("JobRegistryMonitorHelper");
        registryThread.start();
    }

    public void refreshOnline(List<JobGroup> groupList) {
        JobRegistryDao jobRegistryDao = JobAdminConfig.getAdminConfig().getJobRegistryDao();

        List<JobRegistry> onlineList = jobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());

        Map<String, List<JobRegistry>> appAddressMap = onlineList.stream()
                .collect(Collectors.groupingBy(JobRegistry::getRegistryKey));

        // fresh group address
        for (JobGroup group : groupList) {
            List<JobRegistry> registries = appAddressMap.get(group.getAppname());
            if (CollectionUtils.isEmpty(registries)) {
                continue;
            }

            String addressListStr = registries.stream()
                    .map(JobRegistry::getRegistryValue).filter(StringUtils::isNotEmpty)
                    .collect(Collectors.joining(","));

            group.setAddressList(addressListStr);
            JobAdminConfig.getAdminConfig().getJobGroupDao().update(group);
        }
    }

    public void clearDead() {
        List<Integer> ids = JobAdminConfig.getAdminConfig().getJobRegistryDao()
                .findDead(RegistryConfig.DEAD_TIMEOUT, new Date());
        if (ids != null && !ids.isEmpty()) {
            JobAdminConfig.getAdminConfig().getJobRegistryDao().removeDead(ids);
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
