package com.chen.nozdormu.admin.dao;

import com.chen.nozdormu.admin.core.model.JobGroup;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xuxueli on 16/9/30.
 */
@Mapper
public interface JobGroupDao {

    List<JobGroup> findAll();

    List<JobGroup> findByAddressType(@Param("addressType") int addressType);

    int save(JobGroup jobGroup);

    int update(JobGroup jobGroup);

    int remove(@Param("id") int id);

    JobGroup load(@Param("id") int id);

    List<JobGroup> pageList(@Param("offset") int offset,
            @Param("pagesize") int pagesize,
            @Param("appname") String appname,
            @Param("title") String title);

    int pageListCount(@Param("offset") int offset,
            @Param("pagesize") int pagesize,
            @Param("appname") String appname,
            @Param("title") String title);

    JobGroup loadByName(@Param("appName") String appName);
}
