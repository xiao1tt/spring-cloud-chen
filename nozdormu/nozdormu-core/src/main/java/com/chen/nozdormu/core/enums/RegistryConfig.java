package com.chen.nozdormu.core.enums;

/**
 *
 * @author xuxueli
 * @date 17/5/10
 */
public class RegistryConfig {

    public static final int BEAT_TIMEOUT = 10;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 2;

    public enum RegisterType {EXECUTOR, ADMIN}
}
