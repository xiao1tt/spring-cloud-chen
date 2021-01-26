package com.base.commons.cache.core;

import com.google.common.base.Objects;
import java.lang.reflect.Method;

/**
 * @author chenxiaotong
 */
public class CacheMethod {

    private final Object target;
    private final Method method;

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public CacheMethod(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheMethod that = (CacheMethod) o;
        return Objects.equal(target, that.target) &&
                Objects.equal(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(target, method);
    }
}
