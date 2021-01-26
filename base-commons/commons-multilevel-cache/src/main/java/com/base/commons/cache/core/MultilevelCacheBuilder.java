package com.base.commons.cache.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenxiaotong
 */
public class MultilevelCacheBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultilevelCacheBuilder.class);

    public static Cache<MethodCacheKey, Object> buildLocalCache(LocalCacheable localCacheable) {
        return buildLocalCache(localCacheable, null);
    }

    public static Cache<MethodCacheKey, Object> buildLocalCache(LocalCacheable localCacheable,
            CacheLoader<MethodCacheKey, Object> cacheLoader) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();

        if (localCacheable.size() > 0) {
            caffeine.maximumSize(localCacheable.size());
        }

        switch (localCacheable.type()) {
            case CacheType.EXPIRE_AFTER_WRITE:
                caffeine.expireAfterWrite(localCacheable.duration(), localCacheable.unit());
                return caffeine.build();
            case CacheType.EXPIRE_AFTER_ACCESS:
                caffeine.expireAfterAccess(localCacheable.duration(), localCacheable.unit());
                return caffeine.build();
            case CacheType.REFRESH_AFTER_WRITE:
                if (cacheLoader == null) {
                    throw new IllegalArgumentException("REFRESH_AFTER_WRITE 时，cache loader 不能为空)");
                }
                caffeine.refreshAfterWrite(localCacheable.duration(), localCacheable.unit());
                return caffeine.build(cacheLoader);
            default:
                LOGGER.warn("未知的缓存刷新类型，取默认刷新策略。cache:{},type:{}", localCacheable.value(), localCacheable.type());
                caffeine.expireAfterWrite(localCacheable.duration(), localCacheable.unit());
                return caffeine.build();
        }
    }
}
