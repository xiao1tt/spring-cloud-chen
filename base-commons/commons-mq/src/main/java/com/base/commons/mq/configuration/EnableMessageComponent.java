package com.base.commons.mq.configuration;

import com.base.commons.mq.MessageComponentRegistry;
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
@EnableMessageSubscriber
@Import(MessageComponentRegistry.class)
public @interface EnableMessageComponent {

}
