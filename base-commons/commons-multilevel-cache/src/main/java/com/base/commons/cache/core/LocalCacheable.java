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
public @interface LocalCacheable {

    /**
     * cache 名称，用来定位 cache，相同的 value 对应相同的 cache，不填则默认 new 出来一个
     */
    String value() default "";

    /**
     * 使用 SpEL 解析
     */
    String key() default "";

    /**
     * 缓存刷新类型
     *
     * @see CacheType
     */
    int type() default CacheType.EXPIRE_AFTER_WRITE;

    long duration();

    TimeUnit unit();

    int size() default -1;
}
