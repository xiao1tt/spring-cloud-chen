package com.toms.order.center.schedule;

import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.handler.annotation.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author chenxiaotong
 */
@Component
public class ExampleTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Job("test-task")
    public ReturnT<String> task(String param) {
        logger.info("param : {}", param);
        return ReturnT.SUCCESS;
    }
}