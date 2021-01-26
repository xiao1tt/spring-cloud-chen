package com.base.commons.cache.registry;

import com.base.commons.cache.core.CacheMethod;
import com.base.commons.cache.core.LocalCacheable;
import com.base.commons.cache.core.RedisCacheable;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * @author chenxiaotong
 */
@Component
public class MultilevelCacheScanner implements BeanPostProcessor {

    @Resource
    private MultilevelCacheContext multilevelCacheContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Method[] methods = beanClass.getDeclaredMethods();

        if (methods.length == 0) {
            return bean;
        }

        for (Method method : methods) {
            registerLocalCacheDefinition(bean, method);
            registerRedisCacheDefinition(bean, method);
        }

        return bean;
    }

    private void registerRedisCacheDefinition(Object bean, Method method) {
        RedisCacheable cacheable = AnnotationUtils.findAnnotation(method, RedisCacheable.class);

        if (cacheable == null) {
            return;
        }

        if (multilevelCacheContext.getCacheHolder().getRedisClient() == null) {
            throw new RuntimeException("redis cache 初始化失败，未找到 redis client");
        }

        multilevelCacheContext.getCacheDefinitionHolder()
                .registerRedisDefinition(new CacheMethod(bean, method), cacheable);
    }

    private void registerLocalCacheDefinition(Object bean, Method method) {
        LocalCacheable cacheable = AnnotationUtils.findAnnotation(method, LocalCacheable.class);

        if (cacheable == null) {
            return;
        }

        multilevelCacheContext.getCacheDefinitionHolder()
                .registerLocalDefinition(new CacheMethod(bean, method), cacheable);
    }
}
