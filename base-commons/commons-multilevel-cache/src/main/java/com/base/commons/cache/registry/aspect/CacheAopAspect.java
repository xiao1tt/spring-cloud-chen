package com.base.commons.cache.registry.aspect;

import com.base.commons.cache.core.CacheMethod;
import com.base.commons.registry.spel.ExpressionParser;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author chenxiaotong
 */
public abstract class CacheAopAspect {

    protected Object safeProceed(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected CacheMethod getCacheMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Object target = joinPoint.getTarget();
        Class[] argsType = methodSignature.getParameterTypes();
        Method method = target.getClass().getMethod(methodSignature.getName(), argsType);

        return new CacheMethod(target, method);
    }

    protected Object parseExpression(ProceedingJoinPoint joinPoint, String expression) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        List<String> paramNames = Arrays.asList(methodSignature.getParameterNames());
        List<Object> args = Arrays.asList(joinPoint.getArgs());

        Map<String, Object> context = Maps.newHashMap();

        for (int i = 0; i < paramNames.size(); i++) {
            context.put(paramNames.get(i), args.get(i));
        }

        return ExpressionParser.parseExpression(context, expression);
    }

    protected abstract Object defaultKey();

    protected abstract Object getCacheKey(ProceedingJoinPoint joinPoint, CacheMethod cacheMethod);
}
