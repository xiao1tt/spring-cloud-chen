package com.base.commons.cache.core;

import com.base.commons.cache.registry.MultilevelCacheContext;
import com.base.commons.lang.utils.MethodUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenxiaotong
 */
public class MultilevelCacheDefinitionHolder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MultilevelCacheContext context;

    public MultilevelCacheDefinitionHolder(MultilevelCacheContext context) {
        this.context = context;
    }

    private final Map<CacheMethod, LocalCacheable> localCacheableMap = Maps.newConcurrentMap();
    private final Map<CacheMethod, RedisCacheable> redisCacheableMap = Maps.newConcurrentMap();

    private final Map<String, Class<?>> localCacheTypeMap = Maps.newConcurrentMap();

    public void registerRedisDefinition(CacheMethod key, RedisCacheable cacheable) {
        logger.info("cache scan，register redis cache definition，{}", methodName(key));
        redisCacheableMap.put(key, cacheable);
    }

    public void registerLocalDefinition(CacheMethod key, LocalCacheable cacheable) {
        logger.info("cache scan，register local cache definition，{}", methodName(key));

        localCacheableMap.put(key, cacheable);

        if (StringUtils.isNotBlank(cacheable.key())) {
            localCacheTypeMap.put(cacheable.key(), MethodUtil.returnType(key.getMethod()));
        }
    }

    public RedisCacheable getRedisDefinition(CacheMethod cacheMethod) {
        return redisCacheableMap.get(cacheMethod);
    }

    public LocalCacheable getLocalDefinition(CacheMethod cacheMethod) {
        return localCacheableMap.get(cacheMethod);
    }

    public Class<?> getReturnType(String cacheKey) {
        return localCacheTypeMap.get(cacheKey);
    }

    private final Map<CacheMethod, Caffeine<Object, Object>> localCacheBuilderMap = Maps.newConcurrentMap();

    public void registerLocalCacheBuilder(CacheMethod key, Caffeine<Object, Object> caffeine) {
        logger.info("register local cache builder，{}", methodName(key));
        localCacheBuilderMap.put(key, caffeine);
    }

    public Caffeine<Object, Object> getLocalCacheBuilder(CacheMethod key) {
        return localCacheBuilderMap.get(key);
    }

    private String methodName(CacheMethod methodKey) {
        return methodKey.getTarget().getClass().getSimpleName() + "." + methodKey.getMethod().getName();
    }
}
