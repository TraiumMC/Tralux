package me.coderfrish.tralux.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationField {
    String alisa() default "";

    boolean deprecated() default false;

    String[] comments() default {};
}
