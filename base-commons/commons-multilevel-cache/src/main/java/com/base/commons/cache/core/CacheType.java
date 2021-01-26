package com.base.commons.cache.core;

/**
 * @author chenxiaotong
 */
public interface CacheType {

    int EXPIRE_AFTER_WRITE = 1;
    int EXPIRE_AFTER_ACCESS = 2;
    /**
     * 暂时不支持这个策略，以后再说
     */
    int REFRESH_AFTER_WRITE = 3;
}
