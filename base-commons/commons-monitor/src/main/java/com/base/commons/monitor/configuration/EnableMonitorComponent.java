package com.base.commons.monitor.configuration;

import com.base.commons.monitor.MonitorAopAspect;
import com.base.commons.monitor.MonitorRegistry;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author chenxiaotong
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({MonitorRegistry.class, MonitorAopAspect.class})
public @interface EnableMonitorComponent {

}
