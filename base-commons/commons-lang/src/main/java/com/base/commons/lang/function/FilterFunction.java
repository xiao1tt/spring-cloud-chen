package com.base.commons.lang.function;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author chenxiaotong
 */
public interface FilterFunction<T, F> extends BiFunction<List<T>, F, List<T>> {

}
