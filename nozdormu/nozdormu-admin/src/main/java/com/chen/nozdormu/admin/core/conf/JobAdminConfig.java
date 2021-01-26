package com.chen.nozdormu.admin.core.conf;

import com.chen.nozdormu.admin.core.alarm.JobAlarmer;
import com.chen.nozdormu.admin.core.scheduler.JobScheduler;
import com.chen.nozdormu.admin.dao.JobGroupDao;
import com.chen.nozdormu.admin.dao.JobInfoDao;
import com.chen.nozdormu.admin.dao.JobLogDao;
import com.chen.nozdormu.admin.dao.JobLogReportDao;
import com.chen.nozdormu.admin.dao.JobRegistryDao;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * config
 *
 * @author xuxueli 2017-04-28
 */

@Component
public class JobAdminConfig implements InitializingBean, DisposableBean {

    private static JobAdminConfig adminConfig = null;

    public static JobAdminConfig getAdminConfig() {
        return adminConfig;
    }

    // ---------------------- XxlJobScheduler ----------------------

    private JobScheduler jobScheduler;

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;

        jobScheduler = new JobScheduler();
        jobScheduler.init();
    }

    @Override
    public void destroy() throws Exception {
        jobScheduler.destroy();
    }

    // ---------------------- XxlJobScheduler ----------------------

    // conf
    @Value("${nozdormu.job.i18n}")
    private String i18n;

    @Value("${nozdormu.job.accessToken}")
    private String accessToken;

    @Value("${spring.mail.username}")
    private String emailUserName;

    @Value("${nozdormu.job.triggerpool.fast.max}")
    private int triggerPoolFastMax;

    @Value("${nozdormu.job.triggerpool.slow.max}")
    private int triggerPoolSlowMax;

    @Value("${nozdormu.job.logretentiondays}")
    private int logretentiondays;

    // dao, service

    @Resource
    private JobLogDao jobLogDao;
    @Resource
    private JobInfoDao jobInfoDao;
    @Resource
    private JobRegistryDao jobRegistryDao;
    @Resource
    private JobGroupDao jobGroupDao;
    @Resource
    private JobLogReportDao jobLogReportDao;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private DataSource dataSource;
    @Resource
    private JobAlarmer jobAlarmer;


    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmailUserName() {
        return emailUserName;
    }

    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    public int getLogretentiondays() {
        if (logretentiondays < 7) {
            return -1;
        }
        return logretentiondays;
    }

    public JobLogDao getJobLogDao() {
        return jobLogDao;
    }

    public JobInfoDao getJobInfoDao() {
        return jobInfoDao;
    }

    public JobRegistryDao getJobRegistryDao() {
        return jobRegistryDao;
    }

    public JobGroupDao getJobGroupDao() {
        return jobGroupDao;
    }

    public JobLogReportDao getJobLogReportDao() {
        return jobLogReportDao;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JobAlarmer getJobAlarmer() {
        return jobAlarmer;
    }

}
