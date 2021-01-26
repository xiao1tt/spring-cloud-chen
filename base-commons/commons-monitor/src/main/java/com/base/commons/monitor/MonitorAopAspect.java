package com.base.commons.monitor;

import com.base.commons.lang.pojo.BizException;
import com.base.commons.lang.pojo.BizNotification;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author chenxiaotong
 */
@Aspect
public class MonitorAopAspect {

    @Around("@annotation(com.base.commons.monitor.Monitor)")
    public Object cacheAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> clz = joinPoint.getTarget().getClass();
        Class[] argsType = methodSignature.getParameterTypes();
        Method method = clz.getMethod(methodSignature.getName(), argsType);

        Monitor monitor = AnnotationUtils.findAnnotation(method, Monitor.class);
        String name = StringUtils.defaultIfBlank(monitor.value(), clz.getSimpleName() + method.getName());

        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Object result = joinPoint.proceed();
            long cast = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            recordSuccess(name, cast);
            return result;
        } catch (Throwable e) {
            long cast = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            recordError(name, e, cast);
            throw e;
        }
    }

    private void recordSuccess(String name, long cast) {
        Map<String, String> tags = Maps.newHashMap();
        tags.put("result", "success");
        record(name, tags, cast);
    }

    private void recordError(String name, Throwable throwable, long cast) {
        if (throwable instanceof BizNotification) {
            recordSuccess(name, cast);
            return;
        }

        Map<String, String> tags = Maps.newHashMap();
        tags.put("result", throwable instanceof BizException ? "bizException" : "error");

        record(name, tags, cast);
    }

    private void record(String name, Map<String, String> tags, long cast) {
        Monitors.timer(name).tags(tags).register().record(cast, TimeUnit.MILLISECONDS);
    }
}
