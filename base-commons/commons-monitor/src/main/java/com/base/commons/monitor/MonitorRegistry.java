package com.base.commons.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author chenxiaotong
 */
public class MonitorRegistry implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MeterRegistry registry = applicationContext.getBean(MeterRegistry.class);
        Monitors.init(registry);
    }
}
