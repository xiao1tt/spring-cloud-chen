package com.base.commons.web.http.advice;

import com.base.commons.lang.pojo.APIResponse;
import com.base.commons.lang.pojo.BizException;
import com.base.commons.lang.pojo.BizNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author heyawei
 * @date 2020-11-10
 */
@EnableWebMvc
@RestControllerAdvice
public class HybridExceptionAdvice {

    private final Logger logger = LoggerFactory.getLogger(HybridExceptionAdvice.class);

    public HybridExceptionAdvice() {
        super();
    }

    @ExceptionHandler(BizException.class)
    @ResponseBody
    public final APIResponse<Object> exceptionHandler(BizException biz) {
        logger.error("", biz);
        return APIResponse.error(biz.getCode(), biz.getMessage());
    }

    @ExceptionHandler(BizNotification.class)
    @ResponseBody
    public final APIResponse<Object> exceptionHandler(BizNotification biz) {
        logger.error("", biz);
        return APIResponse.error(biz.getCode(), biz.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public final APIResponse<Object> exceptionHandler(NoHandlerFoundException ex) {
        logger.error("", ex);
        return APIResponse.error(HttpStatus.NOT_FOUND.value(), "404");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public final APIResponse<Object> exceptionHandler(Exception ex) {
        logger.error("", ex);
        return APIResponse.error("系统繁忙，请稍后再试");
    }
}
