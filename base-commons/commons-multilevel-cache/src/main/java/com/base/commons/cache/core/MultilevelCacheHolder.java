package com.base.commons.cache.core;

import com.base.commons.cache.registry.MultilevelCacheContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author chenxiaotong
 */
public class MultilevelCacheHolder implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MultilevelCacheContext context;

    public MultilevelCacheHolder(MultilevelCacheContext context) {
        this.context = context;
    }

    private RedisTemplate<String, Object> redisClient;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        redisClient = applicationContext.getBean(RedisTemplate.class);
    }

    private final Map<CacheMethod, Cache<MethodCacheKey, Object>> localCacheMap = Maps.newConcurrentMap();

    private final Map<String, Cache<MethodCacheKey, Object>> localCacheNameMap = Maps.newConcurrentMap();

    private void registerLocal(CacheMethod methodKey, Cache<MethodCacheKey, Object> cache) {
        localCacheMap.put(methodKey, cache);
    }

    public void registerLocal(String name, CacheMethod methodKey, Cache<MethodCacheKey, Object> cache) {
        if (StringUtils.isNotBlank(name)) {
            localCacheNameMap.put(name, cache);
        }

        registerLocal(methodKey, cache);
    }

    public Cache<MethodCacheKey, Object> getLocalCache(String name) {
        return localCacheNameMap.get(name);
    }

    public Cache<MethodCacheKey, Object> getLocalCache(CacheMethod cacheMethod) {
        return localCacheMap.get(cacheMethod);
    }

    public Cache<MethodCacheKey, Object> buildLocalCache(CacheMethod cacheMethod) {
        Cache<MethodCacheKey, Object> cache = localCacheMap.get(cacheMethod);
        if (cache != null) {
            return cache;
        }
        cache = MultilevelCacheBuilder
                .buildLocalCache(context.getCacheDefinitionHolder().getLocalDefinition(cacheMethod));
        localCacheMap.put(cacheMethod, cache);
        return cache;
    }

    public Cache<MethodCacheKey, Object> buildLocalCache(CacheMethod cacheMethod,
            CacheLoader<MethodCacheKey, Object> cacheLoader) {
        Cache<MethodCacheKey, Object> cache = localCacheMap.get(cacheMethod);
        if (cache != null) {
            return cache;
        }
        cache = MultilevelCacheBuilder
                .buildLocalCache(context.getCacheDefinitionHolder().getLocalDefinition(cacheMethod), cacheLoader);
        localCacheMap.put(cacheMethod, cache);
        return cache;
    }

    public RedisTemplate<String, Object> getRedisClient() {
        return redisClient;
    }
}
