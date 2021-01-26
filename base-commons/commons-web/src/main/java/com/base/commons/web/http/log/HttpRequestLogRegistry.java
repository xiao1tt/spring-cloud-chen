package com.base.commons.web.http.log;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenxiaotong
 */
@Configuration
public class HttpRequestLogRegistry {

    @Bean
    public HttpRequestLogFilter httpRequestLogFilter() {
        HttpRequestLogFilter filter = new HttpRequestLogFilter();
        return filter;
    }
}
