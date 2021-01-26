package com.base.commons.web.http.log.support;

import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedStreamUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedStreamUtils.class);

    /**
     * 检查缓存的参数是否有效，无效跑出IllegalArgumentException
     */
    public static void checkCacheSizeParam(int initCacheSize, int maxCacheSize) {
        if (initCacheSize <= 0) {
            throw new IllegalArgumentException("init cache size is invalid!");
        }

        if (maxCacheSize <= 0) {
            throw new IllegalArgumentException("max cache size is valid!");
        }
        if (initCacheSize > maxCacheSize) {
            throw new IllegalArgumentException("init cache is large than max cache size!!");
        }
    }

    /**
     * 安全写入流中，如果遇到异常会吞掉，不抛异常
     */
    public static void safeWrite(OutputStream out, int val) {
        try {
            out.write(val);
        } catch (IOException e) {
            LOGGER.debug("", e);
            //ignore
        }
    }
}
