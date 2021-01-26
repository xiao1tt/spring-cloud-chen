package com.chen.nozdormu.admin.core.thread;

import com.chen.nozdormu.admin.core.conf.JobAdminConfig;
import com.chen.nozdormu.admin.core.model.JobLog;
import com.chen.nozdormu.admin.core.util.I18nUtil;
import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.util.DateUtil;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job lose-monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobLosedMonitorHelper {

    private static Logger logger = LoggerFactory.getLogger(JobLosedMonitorHelper.class);

    private static JobLosedMonitorHelper instance = new JobLosedMonitorHelper();

    public static JobLosedMonitorHelper getInstance() {
        return instance;
    }

    // ---------------------- monitor ----------------------

    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {
        monitorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // monitor
                while (!toStop) {
                    try {
                        // 任务结果丢失处理：调度记录停留在 "运行中" 状态超过10min，且对应执行器心跳注册失败不在线，则将本地调度主动标记失败；
                        Date losedTime = DateUtil.addMinutes(new Date(), -10);
                        List<Long> losedJobIds = JobAdminConfig.getAdminConfig().getJobLogDao()
                                .findLostJobIds(losedTime);

                        if (losedJobIds != null && losedJobIds.size() > 0) {
                            for (Long logId : losedJobIds) {

                                JobLog jobLog = new JobLog();
                                jobLog.setId(logId);

                                jobLog.setHandleTime(new Date());
                                jobLog.setHandleCode(ReturnT.FAIL_CODE);
                                jobLog.setHandleMsg(I18nUtil.getString("joblog_lost_fail"));

                                JobAdminConfig.getAdminConfig().getJobLogDao().updateHandleInfo(jobLog);
                            }

                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(" job fail monitor thread error", e);
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(60);
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                }

                logger.info("JobLosedMonitorHelper stop");

            }
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("  admin JobLosedMonitorHelper");
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
