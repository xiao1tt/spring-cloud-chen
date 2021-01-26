package com.base.commons.cache.registry.aspect;

import com.base.commons.cache.core.CacheMethod;
import com.base.commons.cache.core.CacheType;
import com.base.commons.cache.core.LocalCacheable;
import com.base.commons.cache.core.MethodCacheKey;
import com.base.commons.cache.registry.MultilevelCacheContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author chenxiaotong
 */
@Aspect
public class LocalCacheAopAspect extends CacheAopAspect implements Ordered {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Object defaultKey = new Object();

    @Resource
    private MultilevelCacheContext cacheCoreContext;

    private final Map<MethodCacheKey, Object[]> keyArgsMap = Maps.newConcurrentMap();

    @Around("@annotation(com.base.commons.cache.core.LocalCacheable)")
    public Object cacheAround(ProceedingJoinPoint joinPoint) {
        CacheMethod cacheMethod;

        try {
            cacheMethod = getCacheMethod(joinPoint);
        } catch (NoSuchMethodException e) {
            logger.error("缓存 aop 执行异常，降级执行方法", e);
            return safeProceed(joinPoint);
        }

        LocalCacheable annotation = AnnotationUtils.findAnnotation(cacheMethod.getMethod(), LocalCacheable.class);

        MethodCacheKey cacheKey = new MethodCacheKey(cacheMethod, getCacheKey(joinPoint, cacheMethod));

        keyArgsMap.putIfAbsent(cacheKey, joinPoint.getArgs());

        Cache<MethodCacheKey, Object> cache = cacheCoreContext.getCacheHolder().getLocalCache(cacheMethod);

        if (cache == null) {
            cache = annotation.type() == CacheType.REFRESH_AFTER_WRITE
                    ? cacheCoreContext.getCacheHolder().buildLocalCache(cacheMethod, cacheLoader())
                    : cacheCoreContext.getCacheHolder().buildLocalCache(cacheMethod);
        }

        return cache.get(cacheKey, o -> safeProceed(joinPoint));
    }

    private CacheLoader<MethodCacheKey, Object> cacheLoader() {
        return new CacheLoader<MethodCacheKey, Object>() {
            @Override
            public @Nullable Object load(@NonNull MethodCacheKey key) throws Exception {
                CacheMethod cacheMethod = key.getCacheMethod();
                return cacheMethod.getMethod().invoke(cacheMethod.getTarget(), keyArgsMap.get(key));
            }
        };
    }

    @Override
    protected Object defaultKey() {
        return defaultKey;
    }

    @Override
    protected Object getCacheKey(ProceedingJoinPoint joinPoint, CacheMethod cacheMethod) {
        LocalCacheable localCacheable = cacheCoreContext.getCacheDefinitionHolder().getLocalDefinition(cacheMethod);

        return StringUtils.isNotBlank(localCacheable.key())
                ? parseExpression(joinPoint, localCacheable.key())
                : defaultKey();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
