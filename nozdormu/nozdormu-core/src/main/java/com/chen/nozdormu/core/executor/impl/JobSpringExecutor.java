package com.chen.nozdormu.core.executor.impl;

import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.executor.JobExecutor;
import com.chen.nozdormu.core.handler.annotation.Job;
import com.chen.nozdormu.core.handler.impl.MethodJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;


/**
 * executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 */
public class JobSpringExecutor extends JobExecutor implements ApplicationContextAware, SmartInitializingSingleton,
        DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JobSpringExecutor.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JobSpringExecutor.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // init JobHandler Repository (for method)
        initJobHandlerMethodRepository(applicationContext);

        // super start
        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initJobHandlerMethodRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }

        // init job handler from method
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);

        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            // referred to ：org.springframework.context.event.EventListenerMethodProcessor.processBean
            Map<Method, Job> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MetadataLookup<Job>) method -> AnnotatedElementUtils
                                .findMergedAnnotation(method, Job.class));
            } catch (Throwable ex) {
                logger.error("method-jobhandler resolve error for bean[{}].", beanDefinitionName, ex);
            }

            if (annotatedMethods == null || annotatedMethods.isEmpty()) {
                continue;
            }

            for (Map.Entry<Method, Job> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method method = methodXxlJobEntry.getKey();
                Job job = methodXxlJobEntry.getValue();
                if (job == null) {
                    continue;
                }

                String name = job.value();
                if (name.trim().length() == 0) {
                    throw new RuntimeException("method-jobhandler name invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                }

                if (loadJobHandler(name) != null) {
                    throw new RuntimeException("method-jobhandler[" + name + "] naming conflicts.");
                }

                // execute method
                if (!(method.getParameterTypes().length == 1 && method.getParameterTypes()[0]
                        .isAssignableFrom(String.class))) {
                    throw new RuntimeException("method-jobhandler param-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " + "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }

                if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
                    throw new RuntimeException("method-jobhandler return-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " + "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }

                method.setAccessible(true);

                // init and destroy
                Method initMethod = null;
                Method destroyMethod = null;

                if (job.init().trim().length() > 0) {
                    try {
                        initMethod = bean.getClass().getDeclaredMethod(job.init());
                        initMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("method-jobhandler initMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }

                if (job.destroy().trim().length() > 0) {
                    try {
                        destroyMethod = bean.getClass().getDeclaredMethod(job.destroy());
                        destroyMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("method-jobhandler destroyMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }

                // registry job handler
                registerJobHandler(name, new MethodJobHandler(bean, method, initMethod, destroyMethod));
            }
        }

    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
