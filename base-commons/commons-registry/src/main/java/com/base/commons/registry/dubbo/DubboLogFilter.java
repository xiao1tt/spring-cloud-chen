package com.base.commons.registry.dubbo;

import static org.apache.dubbo.common.constants.CommonConstants.GROUP_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.VERSION_KEY;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.support.AccessLogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenxiaotong
 */
public class DubboLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger("dubbo");

    /**
     * This method logs the access log for service method invocation call.
     *
     * @param invoker service
     * @param inv Invocation service method.
     * @return Result from service method.
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation inv) throws RpcException {
        AccessLogData requestLogDate = buildAccessLogData(invoker, inv);

        Result result = invoker.invoke(inv);

        LOGGER.info("request:{}", requestLogDate.getLogMessage());

        if (result instanceof AsyncRpcResult) {
            CompletableFuture<AppResponse> responseFuture = ((AsyncRpcResult) result).getResponseFuture();

            responseFuture.whenComplete((appResponse, throwable) -> {
                if (throwable == null) {
                    LOGGER.info("response:{}", appResponse);
                } else {
                    LOGGER.error("error", throwable);
                }
            });
        }

        return result;
    }

    private AccessLogData buildAccessLogData(Invoker<?> invoker, Invocation inv) {
        AccessLogData logData = AccessLogData.newLogData();
        logData.setServiceName(invoker.getInterface().getName());
        logData.setMethodName(inv.getMethodName());
        logData.setVersion(invoker.getUrl().getParameter(VERSION_KEY));
        logData.setGroup(invoker.getUrl().getParameter(GROUP_KEY));
        logData.setInvocationTime(new Date());
        logData.setTypes(inv.getParameterTypes());
        logData.setArguments(inv.getArguments());
        return logData;
    }
}
