package com.base.commons.cache.core;

import com.google.common.base.Objects;

/**
 * @author chenxiaotong
 */
public class MethodCacheKey {

    private final CacheMethod cacheMethod;
    private final Object key;

    public MethodCacheKey(CacheMethod cacheMethod, Object key) {
        this.cacheMethod = cacheMethod;
        this.key = key;
    }

    public CacheMethod getCacheMethod() {
        return cacheMethod;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodCacheKey that = (MethodCacheKey) o;
        return Objects.equal(cacheMethod, that.cacheMethod) &&
                Objects.equal(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cacheMethod, key);
    }
}
