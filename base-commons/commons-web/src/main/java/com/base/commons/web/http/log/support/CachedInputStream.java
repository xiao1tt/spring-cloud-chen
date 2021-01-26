package com.base.commons.web.http.log.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * 可以缓存从流中读取的数据的代理流类，用于日志记录
 */
public class CachedInputStream extends ServletInputStream implements CachedStream {

    private final ByteArrayOutputStream cachedOutputStream;
    private final ServletInputStream srcInputStream;
    private final int maxCacheSize;

    public CachedInputStream(ServletInputStream srcInputStream, int initCacheSize, int maxCacheSize) {
        CachedStreamUtils.checkCacheSizeParam(initCacheSize, maxCacheSize);
        this.srcInputStream = srcInputStream;
        this.cachedOutputStream = new ByteArrayOutputStream(initCacheSize);
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    public int read() throws IOException {
        int b = srcInputStream.read();
        if (b != -1 && cachedOutputStream.size() < maxCacheSize) {
            CachedStreamUtils.safeWrite(cachedOutputStream, b);
        }
        return b;
    }

    @Override
    public byte[] getCached() {
        return cachedOutputStream.toByteArray();
    }

    @Override
    public void close() throws IOException {
        super.close();
        cachedOutputStream.close();
    }

    @Override
    public boolean isFinished() {
        return srcInputStream.isFinished();
    }

    @Override
    public boolean isReady() {
        return srcInputStream.isReady();
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}
