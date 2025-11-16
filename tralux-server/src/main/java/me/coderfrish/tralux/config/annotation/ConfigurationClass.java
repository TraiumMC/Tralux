package me.coderfrish.tralux.config.annotation;

import me.coderfrish.tralux.config.ConfigurationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationClass {
    String name();

    ConfigurationType type();

    boolean deprecated() default false;

    String[] comments() default {};
}
