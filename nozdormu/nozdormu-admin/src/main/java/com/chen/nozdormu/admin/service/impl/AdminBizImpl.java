package com.chen.nozdormu.admin.service.impl;

import com.chen.nozdormu.admin.core.model.JobGroup;
import com.chen.nozdormu.admin.core.model.JobInfo;
import com.chen.nozdormu.admin.core.model.JobLog;
import com.chen.nozdormu.admin.core.model.JobRegistry;
import com.chen.nozdormu.admin.core.thread.JobTriggerPoolHelper;
import com.chen.nozdormu.admin.core.trigger.TriggerTypeEnum;
import com.chen.nozdormu.admin.core.util.I18nUtil;
import com.chen.nozdormu.admin.dao.JobGroupDao;
import com.chen.nozdormu.admin.dao.JobInfoDao;
import com.chen.nozdormu.admin.dao.JobLogDao;
import com.chen.nozdormu.admin.dao.JobRegistryDao;
import com.chen.nozdormu.admin.service.JobService;
import com.chen.nozdormu.core.biz.AdminBiz;
import com.chen.nozdormu.core.biz.model.HandleCallbackParam;
import com.chen.nozdormu.core.biz.model.RegistryParam;
import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.enums.RegistryConfig;
import com.chen.nozdormu.core.handler.IJobHandler;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {

    private static Logger logger = LoggerFactory.getLogger(AdminBizImpl.class);

    @Resource
    public JobLogDao jobLogDao;
    @Resource
    private JobService jobService;
    @Resource
    private JobInfoDao jobInfoDao;
    @Resource
    private JobRegistryDao jobRegistryDao;
    @Resource
    private JobGroupDao jobGroupDao;

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam : callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            logger.debug("> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode() == IJobHandler.SUCCESS.getCode() ? "success" : "fail"),
                    handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLog log = jobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            // avoid repeat callback, trigger child job etc
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");
        }

        // trigger success, to trigger child job
        String callbackMsg = null;
        if (IJobHandler.SUCCESS.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
            JobInfo jobInfo = jobInfoDao.loadById(log.getJobId());
            if (jobInfo != null && jobInfo.getChildJobId() != null
                    && jobInfo.getChildJobId().trim().length() > 0) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + I18nUtil
                        .getString("jobconf_trigger_child_run") + "<<<<<<<<<<< </span><br>";

                String[] childJobIds = jobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId =
                            (childJobIds[i] != null && childJobIds[i].trim().length() > 0 && isNumeric(childJobIds[i]))
                                    ? Integer.valueOf(childJobIds[i]) : -1;
                    if (childJobId > 0) {

                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        // add msg
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
                                (i + 1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode() == ReturnT.SUCCESS_CODE ? I18nUtil
                                        .getString("system_success") : I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
                                (i + 1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        if (handleMsg.length() > 15000) {
            // text最大64kb 避免长度过长
            handleMsg = new StringBuffer(handleMsg.substring(0, 15000));
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        jobLogDao.updateHandleInfo(log);

        return ReturnT.SUCCESS;
    }

    private boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {

        // valid
        if (StringUtils.isBlank(registryParam.getRegistryGroup())
                || StringUtils.isBlank(registryParam.getRegistryKey())
                || StringUtils.isBlank(registryParam.getRegistryValue())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        JobGroup group = refreshGroup(registryParam);

        int ret = jobRegistryDao.registryUpdate(registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                registryParam.getRegistryValue(), new Date());

        if (ret < 1) {
            jobRegistryDao.registrySave(registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                    registryParam.getRegistryValue(), new Date());
        }

        // fresh
        refreshGroupAddress(group);
        refreshJobs(group, registryParam.getRegistryJobs());

        return ReturnT.SUCCESS;
    }

    private void refreshJobs(JobGroup group, String registryJobs) {
        if (StringUtils.isBlank(registryJobs)) {
            return;
        }

        Set<String> exists = jobInfoDao.getJobsByGroup(group.getId()).stream()
                .map(JobInfo::getExecutorHandler).collect(Collectors.toSet());

        Set<String> newJobs = Arrays.stream(registryJobs.split(","))
                .filter(job -> !exists.contains(job)).collect(Collectors.toSet());

        for (String newJob : newJobs) {
            JobInfo jobInfo = new JobInfo();
            jobInfo.setJobGroup(group.getId());
            jobInfo.setJobCron("0 0 * * * ?");
            jobInfo.setJobDesc(newJob);
            jobInfo.setAuthor(group.getAppname());
            jobInfo.setExecutorRouteStrategy("ROUND");
            jobInfo.setExecutorHandler(newJob);
            jobInfo.setExecutorParam("");
            jobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
            jobInfo.setGlueType("BEAN");
            jobInfo.setGlueSource("");
            jobInfo.setGlueRemark("");
            jobInfo.setChildJobId("");
            jobInfo.setTriggerStatus(0);
            jobInfo.setTriggerLastTime(0L);
            jobInfo.setTriggerNextTime(0L);

            jobService.add(jobInfo);
        }
    }

    private void refreshGroupAddress(JobGroup group) {
        List<JobRegistry> online = jobRegistryDao.findOnline(RegistryConfig.DEAD_TIMEOUT, group.getAppname());
        String addressListStr = online.stream()
                .map(JobRegistry::getRegistryValue).filter(org.apache.commons.lang3.StringUtils::isNotEmpty)
                .collect(Collectors.joining(","));
        group.setAddressList(addressListStr);
        jobGroupDao.update(group);
    }

    private void refreshGroupAddress(String groupName) {
        JobGroup group = jobGroupDao.loadByName(groupName);
        refreshGroupAddress(group);
    }

    private JobGroup refreshGroup(RegistryParam registryParam) {
        JobGroup group = jobGroupDao.loadByName(registryParam.getRegistryKey());

        if (group == null) {
            group = new JobGroup();
            group.setAppname(registryParam.getRegistryKey());
            group.setTitle(registryParam.getRegistryKey());
            group.setAddressType(0);

            jobGroupDao.save(group);
        }

        return group;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {

        // valid
        if (StringUtils.isBlank(registryParam.getRegistryGroup())
                || StringUtils.isBlank(registryParam.getRegistryKey())
                || StringUtils.isBlank(registryParam.getRegistryValue())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        int ret = jobRegistryDao.registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(),
                registryParam.getRegistryValue());

        if (ret > 0) {
            // fresh
            refreshGroupAddress(registryParam.getRegistryKey());
        }

        return ReturnT.SUCCESS;
    }
}
