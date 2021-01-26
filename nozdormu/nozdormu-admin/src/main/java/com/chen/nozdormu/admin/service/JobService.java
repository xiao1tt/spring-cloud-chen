package com.chen.nozdormu.admin.service;


import com.chen.nozdormu.admin.core.model.JobInfo;
import com.chen.nozdormu.core.biz.model.ReturnT;
import java.util.Date;
import java.util.Map;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface JobService {

    /**
     * page list
     */
    Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc,
            String executorHandler, String author);

    /**
     * add job
     */
    ReturnT<String> add(JobInfo jobInfo);

    /**
     * update job
     */
    ReturnT<String> update(JobInfo jobInfo);

    /**
     * remove job
     * *
     */
    ReturnT<String> remove(int id);

    /**
     * start job
     */
    ReturnT<String> start(int id);

    /**
     * stop job
     */
    ReturnT<String> stop(int id);

    /**
     * dashboard info
     */
    Map<String, Object> dashboardInfo();

    /**
     * chart info
     */
    ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);

}
