package com.chen.nozdormu.admin.dao;

import com.chen.nozdormu.admin.core.model.JobLogGlue;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * job log for glue
 *
 * @author xuxueli 2016-5-19 18:04:56
 */
@Mapper
public interface JobLogGlueDao {

    int save(JobLogGlue jobLogGlue);

    List<JobLogGlue> findByJobId(@Param("jobId") int jobId);

    int removeOld(@Param("jobId") int jobId, @Param("limit") int limit);

    int deleteByJobId(@Param("jobId") int jobId);

}
