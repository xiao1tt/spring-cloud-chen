package com.base.commons.cache.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author chenxiaotong
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCacheable {

    /**
     * 对应 redis 里的 key prefix
     */
    String prefix();

    /**
     * redis key, 使用 SpEL 解析
     */
    String key();

    long duration();

    TimeUnit unit();
}
