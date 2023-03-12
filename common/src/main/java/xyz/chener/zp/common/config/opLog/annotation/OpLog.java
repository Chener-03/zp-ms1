package xyz.chener.zp.common.config.opLog.annotation;


import org.springframework.core.annotation.AliasFor;
import xyz.chener.zp.common.config.opLog.processer.impl.DefaultStdOutOpRecord;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {


    String operateName();

    Class recordClass() default DefaultStdOutOpRecord.class;
}
