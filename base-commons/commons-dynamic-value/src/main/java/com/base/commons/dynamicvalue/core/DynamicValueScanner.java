package com.base.commons.dynamicvalue.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author chenxiaotong
 */
public class DynamicValueScanner implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        scanValueAnnotation(bean, beanClass);
        scanFileAnnotation(bean, beanClass);

        return bean;
    }

    private void scanFileAnnotation(Object bean, Class<?> beanClass) {
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            NacosFile annotation = AnnotationUtils.findAnnotation(method, NacosFile.class);
            if (annotation == null) {
                continue;
            }

            String group = annotation.group();
            String value = annotation.file();

            DynamicValueListenerHolder.getInstance().register(group, resolveKey(value), bean, method);
        }
    }

    private void scanValueAnnotation(Object bean, Class<?> beanClass) {
        Field[] fields = beanClass.getDeclaredFields();

        for (Field field : fields) {
            Value value = AnnotationUtils.findAnnotation(field, Value.class);
            if (value == null) {
                continue;
            }

            String key = value.value();
            DynamicValueListenerHolder.getInstance().register(resolveKey(key), bean, field);
        }
    }

    private final String placeholderPrefix = "${";

    private final String placeholderSuffix = "}";

    private final String valueSeparator = ":";

    private String resolveKey(String originString) {
        if (!originString.startsWith(placeholderPrefix)) {
            return originString;
        }

        int start = originString.indexOf(placeholderPrefix);
        int end = originString.indexOf(placeholderSuffix);
        if (originString.contains(valueSeparator)) {
            end = originString.indexOf(valueSeparator);
        }

        return originString.substring(start + placeholderPrefix.length(), end);
    }
}
