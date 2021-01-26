package com.base.commons.web.http.log;

import com.base.commons.web.http.log.support.CachedHttpServletRequestWrapper;
import com.base.commons.web.http.log.support.CachedHttpServletResponseWrapper;
import com.base.commons.web.http.log.support.CachedStream;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author chenxiaotong
 */
@WebFilter
@Order
public class HttpRequestLogFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestLogFilter.class);

    private static final String DEFAULT_IGNORE_TEXT = "{not text or too large, ignored}";
    private static final String DEFAULT_LOGGER_NAME = "requestLog";

    private static final int MAX_CACHE_LEN = 2 * 1024 * 1024;
    private static final int INIT_CACHE_LEN = 512 * 1024;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${http.log.ignore.url:}")
    private List<String> ignoreUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();

        boolean ignore = ignoreUrls.stream().anyMatch(ignoreUrl -> pathMatcher.match(ignoreUrl, uri));

        if (ignore) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();

        CachedHttpServletRequestWrapper httpServletRequestWrapper = new CachedHttpServletRequestWrapper(request,
                INIT_CACHE_LEN, MAX_CACHE_LEN);
        CachedHttpServletResponseWrapper httpServletResponseWrapper = new CachedHttpServletResponseWrapper(response,
                INIT_CACHE_LEN, MAX_CACHE_LEN);
        try {
            filterChain.doFilter(httpServletRequestWrapper, httpServletResponseWrapper);
        } finally {
            long end = System.currentTimeMillis();
            saveLogData(request, httpServletRequestWrapper, httpServletResponseWrapper, end - start);
        }
    }

    private void saveLogData(HttpServletRequest request,
            CachedHttpServletRequestWrapper httpServletRequestWrapper,
            CachedHttpServletResponseWrapper httpServletResponseWrapper, long time) {
        try {
            // 如果使用了Writer就需要刷新流
            httpServletRequestWrapper.flushStream();
            httpServletResponseWrapper.flushStream();

            byte[] requestData = httpServletRequestWrapper.getCachedStream().getCached();
            byte[] responseData = httpServletResponseWrapper.getCachedStream().getCached();

            String requestString = requestData == null ? StringUtils.EMPTY : new String(requestData);
            String responseString = responseData == null ? StringUtils.EMPTY : new String(responseData);

            // 非 text 内容，隐藏
            if (!isTextContentType(httpServletRequestWrapper.getContentType())) {
                requestString = DEFAULT_IGNORE_TEXT;
            }

            if (!isTextContentType(httpServletResponseWrapper.getContentType())) {
                responseString = DEFAULT_IGNORE_TEXT;
            }

            // 处理请求参数map
            Map params = request.getParameterMap();
            params = Maps.newHashMap(params);
            String paramString = StringUtils.EMPTY;
            List<String> pairs = Lists.newArrayList();
            if (MapUtils.isNotEmpty(params)) {
                for (Object name : params.keySet()) {
                    Object value = params.get(name);
                    if (value instanceof String) {
                        pairs.add(name + "=" + StringUtils.trimToEmpty((String) value));
                    } else if (value instanceof String[]) {
                        String[] values = (String[]) value;
                        for (String v : values) {
                            pairs.add(name + "=" + StringUtils.trimToEmpty(v));
                        }
                    } else if (value != null) {
                        pairs.add(name + "=" + value.toString());
                    }
                }
                paramString = Joiner.on("&").join(pairs);
            }

            if (StringUtils.equals(request.getContentType(), MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                paramString = URLDecoder.decode(paramString, "UTF-8");
            }

            // 使用logger记录日志
            Logger logger = LoggerFactory.getLogger(DEFAULT_LOGGER_NAME);

            StringBuilder buffer = new StringBuilder();
            buffer.append(logString("process_time", time + " ms"));
            buffer.append(logString("uri", request.getRequestURI() + "?" + paramString));
            buffer.append(logString("request", requestString.replaceAll("\n|\r", "")));
            buffer.append(logString("response", responseString.replaceAll("\n|\r", "")));
            logger.info(buffer.toString());
        } catch (Exception e) {
            LOGGER.warn("log request data error", e);
        } finally {
            closeQuietly(httpServletRequestWrapper.getCachedStream());
            closeQuietly(httpServletResponseWrapper.getCachedStream());
        }
    }

    private String logString(String key, String value) {
        return "[" + key + "=" + value + "]";
    }

    private void closeQuietly(CachedStream cachedStream) {
        if (cachedStream instanceof InputStream) {
            IOUtils.closeQuietly((InputStream) cachedStream);
        }
        if (cachedStream instanceof OutputStream) {
            IOUtils.closeQuietly((OutputStream) cachedStream);
        }
    }

    private static final Set<String> TEXT_CONTENT_TYPES = Sets.newHashSet(ContentType.APPLICATION_JSON.getMimeType(),
            ContentType.APPLICATION_XML.getMimeType(), ContentType.TEXT_HTML.getMimeType(),
            ContentType.TEXT_XML.getMimeType(), ContentType.TEXT_PLAIN.getMimeType());

    private boolean isTextContentType(String contentType) {
        if (contentType == null) {
            return true;
        }

        for (String textContentType : TEXT_CONTENT_TYPES) {
            if (contentType.contains(textContentType)) {
                return true;
            }
        }

        return false;
    }
}
