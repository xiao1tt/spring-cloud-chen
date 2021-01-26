package com.base.commons.dynamicvalue.configuration;

import com.base.commons.dynamicvalue.core.DynamicValueCoreContext;
import com.base.commons.dynamicvalue.core.DynamicValueScanner;
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
@Import({DynamicValueCoreContext.class, DynamicValueScanner.class})
public @interface EnableDynamicValue {

}
