package com.base.commons.mq;

/**
 * @author chenxiaotong
 */

public enum OrderMode {
    /**
     * 顺序消费
     */
    ORDERLY,
    /**
     * 并发消费
     */
    CONCURRENTLY,
    ;
}
