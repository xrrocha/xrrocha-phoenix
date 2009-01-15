package com.sun.phoenix.util.beanform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScalarProperty {
	String prompt() default "";
	String fyi() default "";
	int length() default 16;
	String defaultValue() default "";
	int numLines() default 0;
}
