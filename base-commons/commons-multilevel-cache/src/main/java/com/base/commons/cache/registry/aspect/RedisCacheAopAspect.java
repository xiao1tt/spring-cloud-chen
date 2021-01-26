package com.base.commons.cache.registry.aspect;

import com.base.commons.cache.core.CacheMethod;
import com.base.commons.cache.core.RedisCacheable;
import com.base.commons.cache.registry.MultilevelCacheContext;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author chenxiaotong
 */
@Aspect
public class RedisCacheAopAspect extends CacheAopAspect implements Ordered {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MultilevelCacheContext cacheCoreContext;

    @Around("@annotation(com.base.commons.cache.core.RedisCacheable)")
    public Object cacheAround(ProceedingJoinPoint joinPoint) {
        RedisTemplate<String, Object> redisTemplate = cacheCoreContext.getCacheHolder().getRedisClient();

        if (redisTemplate == null) {
            return safeProceed(joinPoint);
        }

        CacheMethod cacheMethod;

        try {
            cacheMethod = getCacheMethod(joinPoint);
        } catch (NoSuchMethodException e) {
            logger.error("缓存 aop 执行异常，降级执行方法", e);
            return safeProceed(joinPoint);
        }

        Method method = cacheMethod.getMethod();

        RedisCacheable redisCacheable = AnnotationUtils.findAnnotation(method, RedisCacheable.class);

        String cacheKey = getCacheKey(joinPoint, cacheMethod).toString();

        if (StringUtils.isBlank(cacheKey)) {
            logger.warn("redis cache warning， key parse result is null，execute method");
            return safeProceed(joinPoint);
        }

        String redisKey = redisCacheable.prefix() + "@" + cacheKey;

        Object value = redisTemplate.opsForValue().get(redisKey);

        if (value != null || redisTemplate.hasKey(redisKey)) {
            return value;
        }

        Object result = safeProceed(joinPoint);
        redisTemplate.opsForValue().set(redisKey, result, redisCacheable.duration(), redisCacheable.unit());

        return result;
    }

    @Override
    protected Object defaultKey() {
        return "";
    }

    @Override
    protected Object getCacheKey(ProceedingJoinPoint joinPoint, CacheMethod cacheMethod) {
        RedisCacheable localCacheable = cacheCoreContext.getCacheDefinitionHolder().getRedisDefinition(cacheMethod);

        return StringUtils.isNotBlank(localCacheable.key())
                ? parseExpression(joinPoint, localCacheable.key())
                : defaultKey();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
