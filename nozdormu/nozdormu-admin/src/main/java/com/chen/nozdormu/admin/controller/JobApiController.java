package com.chen.nozdormu.admin.controller;

import com.chen.nozdormu.admin.controller.annotation.PermissionLimit;
import com.chen.nozdormu.admin.core.conf.JobAdminConfig;
import com.chen.nozdormu.core.biz.AdminBiz;
import com.chen.nozdormu.core.biz.model.HandleCallbackParam;
import com.chen.nozdormu.core.biz.model.RegistryParam;
import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.util.GsonTool;
import com.chen.nozdormu.core.util.JobRemotingUtil;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
@RequestMapping("/api")
public class JobApiController {

    @Resource
    private AdminBiz adminBiz;

    /**
     * api
     */
    @RequestMapping("/{uri}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> api(HttpServletRequest request, @PathVariable("uri") String uri,
            @RequestBody(required = false) String data) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri == null || uri.trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (JobAdminConfig.getAdminConfig().getAccessToken() != null
                && JobAdminConfig.getAdminConfig().getAccessToken().trim().length() > 0
                && !JobAdminConfig.getAdminConfig().getAccessToken()
                .equals(request.getHeader(JobRemotingUtil.JOB_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // services mapping
        if ("callback".equals(uri)) {
            List<HandleCallbackParam> callbackParamList = GsonTool
                    .fromJson(data, List.class, HandleCallbackParam.class);
            return adminBiz.callback(callbackParamList);
        } else if ("registry".equals(uri)) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return adminBiz.registry(registryParam);
        } else if ("registryRemove".equals(uri)) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return adminBiz.registryRemove(registryParam);
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
        }

    }

}
