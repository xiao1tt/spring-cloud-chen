package com.chen.nozdormu.admin.core.alarm;

import com.chen.nozdormu.admin.core.model.JobInfo;
import com.chen.nozdormu.admin.core.model.JobLog;

/**
 * @author xuxueli 2020-01-19
 */
public interface JobAlarm {

    /**
     * job alarm
     */
    public boolean doAlarm(JobInfo info, JobLog jobLog);

}
