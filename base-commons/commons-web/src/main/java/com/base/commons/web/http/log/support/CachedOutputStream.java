package com.base.commons.web.http.log.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * 可以缓存写入流中的数据的代理流类，用于日志记录
 */
public class CachedOutputStream extends ServletOutputStream implements CachedStream {

    private final ByteArrayOutputStream cachedOutputStream;
    private final ServletOutputStream srcOutputStream;
    private final int maxCacheSize;

    public CachedOutputStream(ServletOutputStream srcOutputStream, int initCacheSize, int maxCacheSize) {
        CachedStreamUtils.checkCacheSizeParam(initCacheSize, maxCacheSize);
        this.srcOutputStream = srcOutputStream;
        this.cachedOutputStream = new ByteArrayOutputStream(initCacheSize);
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    public byte[] getCached() {
        return cachedOutputStream.toByteArray();
    }

    @Override
    public void write(int b) throws IOException {
        this.srcOutputStream.write(b);
        if (this.cachedOutputStream.size() < maxCacheSize) {
            CachedStreamUtils.safeWrite(cachedOutputStream, b);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.cachedOutputStream.close();
    }

    @Override
    public boolean isReady() {
        return srcOutputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }
}
