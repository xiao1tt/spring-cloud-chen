package com.chen.nozdormu.admin.dao;

import com.chen.nozdormu.admin.core.model.JobRegistry;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xuxueli on 16/9/30.
 */
@Mapper
public interface JobRegistryDao {

    List<Integer> findDead(@Param("timeout") int timeout,
            @Param("nowTime") Date nowTime);

    int removeDead(@Param("ids") List<Integer> ids);

    List<JobRegistry> findAll(@Param("timeout") int timeout,
            @Param("nowTime") Date nowTime);

    int registryUpdate(@Param("registryGroup") String registryGroup,
            @Param("registryKey") String registryKey,
            @Param("registryValue") String registryValue,
            @Param("updateTime") Date updateTime);

    int registrySave(@Param("registryGroup") String registryGroup,
            @Param("registryKey") String registryKey,
            @Param("registryValue") String registryValue,
            @Param("updateTime") Date updateTime);

    int registryDelete(@Param("registryGroup") String registryGroup,
            @Param("registryKey") String registryKey,
            @Param("registryValue") String registryValue);

    List<JobRegistry> findOnline(@Param("timeout") int timeout, @Param("registryKey") String registryKey);
}
