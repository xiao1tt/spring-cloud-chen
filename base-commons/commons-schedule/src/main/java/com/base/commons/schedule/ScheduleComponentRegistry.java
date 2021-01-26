package com.base.commons.schedule;

import com.chen.nozdormu.core.executor.impl.JobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenxiaotong
 */
@Configuration
public class ScheduleComponentRegistry {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${nozdormu.job.admin.addresses}")
    private String adminAddresses;

    @Value("${spring.application.name}")
    private String appname;

    @Bean
    public JobSpringExecutor nozdormuJobExecutor() {
        logger.info("nozdormu-job config init");
        JobSpringExecutor jobSpringExecutor = new JobSpringExecutor();
        jobSpringExecutor.setAdminAddresses(adminAddresses);
        jobSpringExecutor.setAppname(appname);
        jobSpringExecutor.setLogRetentionDays(30);
        return jobSpringExecutor;
    }
}
