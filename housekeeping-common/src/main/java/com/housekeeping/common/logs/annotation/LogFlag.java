package com.housekeeping.common.logs.annotation;

import java.lang.annotation.*;

/**
 * @Author su
 * @create 2020/10/26 22:27
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogFlag {
    /**
     * 描述
     * @return {String}
     */
    String description();
}
