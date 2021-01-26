package com.base.commons.cache.registry;

import com.base.commons.cache.core.MultilevelCacheDefinitionHolder;
import com.base.commons.cache.core.MultilevelCacheHolder;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author chenxiaotong
 */
@Component
public class MultilevelCacheContext implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicBoolean INIT = new AtomicBoolean(false);

    private MultilevelCacheDefinitionHolder cacheDefinitionHolder;

    private MultilevelCacheHolder cacheHolder;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (INIT.compareAndSet(false, true)) {
            cacheDefinitionHolder = new MultilevelCacheDefinitionHolder(this);
            cacheHolder = new MultilevelCacheHolder(this);
            logger.info("multilevel cache context init success");
        }
    }

    public MultilevelCacheDefinitionHolder getCacheDefinitionHolder() {
        return cacheDefinitionHolder;
    }

    public MultilevelCacheHolder getCacheHolder() {
        return cacheHolder;
    }
}
