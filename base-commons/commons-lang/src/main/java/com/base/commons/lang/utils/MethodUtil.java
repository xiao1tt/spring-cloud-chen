package com.base.commons.lang.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * 工具类
 *
 * @author chenxiaotong
 */
public class MethodUtil {

    public static Class<?> returnType(Method method) {
        Class<?> returnType = method.getReturnType();
        return Collection.class.isAssignableFrom(returnType)
                ? ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].getClass()
                : returnType;
    }
}
