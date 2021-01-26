package com.base.commons.web.configuration;

import com.base.commons.cache.configuration.EnableMultilevelCache;
import com.base.commons.dynamicvalue.configuration.EnableDynamicValue;
import com.base.commons.monitor.configuration.EnableMonitorComponent;
import com.base.commons.mq.configuration.EnableMessageComponent;
import com.base.commons.schedule.EnableNozdormuSchedule;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @author chenxiaotong
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDiscoveryClient
@EnableDynamicValue
@EnableMonitorComponent
@EnableMultilevelCache
@EnableMessageComponent
@EnableNozdormuSchedule
@Import(WebComponentRegistry.class)
public @interface EnableWebComponent {

}
