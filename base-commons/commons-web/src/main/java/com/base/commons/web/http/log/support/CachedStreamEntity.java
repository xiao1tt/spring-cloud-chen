package com.base.commons.web.http.log.support;

public interface CachedStreamEntity {

    CachedStream getCachedStream();

    void flushStream();
}
