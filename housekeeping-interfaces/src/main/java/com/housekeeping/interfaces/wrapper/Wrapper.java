package com.housekeeping.interfaces.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author su
 * @Date 2020/12/4 17:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Wrapper {

    /** 条件的关键字 */
    String keyword() default "eq";

    /** 字段名 */
    String field();

}
