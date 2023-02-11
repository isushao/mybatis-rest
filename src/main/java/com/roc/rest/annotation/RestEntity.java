package com.roc.rest.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestEntity {

    /**
     * Name of the class in the metadata.
     */
    String name() default "";


    /**
     * 默认情况下，类的所有属性都包含在元数据中。
     */
    boolean annotatedPropertiesOnly() default false;
}
