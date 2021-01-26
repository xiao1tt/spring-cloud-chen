package com.base.commons.web.http.advice;

import com.alibaba.fastjson.JSON;
import com.base.commons.lang.pojo.APIResponse;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author heyawei
 * @date 2020-11-10
 */
@RestControllerAdvice
public class StandardResponseAdvice implements ResponseBodyAdvice<Object>, EnvironmentAware {

    private String monitorPath = "/actuator";

    @PostConstruct
    private void init() {
        monitorPath = StringUtils.defaultIfBlank(environment.getProperty("management.endpoints.web.base-path"),
                monitorPath);
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
            Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
            ServerHttpResponse serverHttpResponse) {
        if (isMonitorRequest(serverHttpRequest)) {
            return o;
        }
        // String特殊处理，否则会抛异常
        if (o instanceof String || StringHttpMessageConverter.class.isAssignableFrom(aClass)) {
            return JSON.toJSON(APIResponse.success(o)).toString();
        }
        // 判断为null构建ResponseData对象进行返回
        if (Objects.isNull(o)) {
            return APIResponse.success();
        }

        if (APIResponse.class.isAssignableFrom(o.getClass())) {
            return o;
        }

        return APIResponse.success(o);
    }

    private boolean isMonitorRequest(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getURI().getPath().startsWith(monitorPath);
    }

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
