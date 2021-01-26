package com.chen.nozdormu.admin.dao;

import com.chen.nozdormu.admin.core.model.JobLogReport;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * job log
 *
 * @author xuxueli 2019-11-22
 */
@Mapper
public interface JobLogReportDao {

    int save(JobLogReport jobLogReport);

    int update(JobLogReport jobLogReport);

    List<JobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
            @Param("triggerDayTo") Date triggerDayTo);

    JobLogReport queryLogReportTotal();

}
