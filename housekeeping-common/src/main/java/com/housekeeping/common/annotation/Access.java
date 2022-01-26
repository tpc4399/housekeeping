package com.housekeeping.common.annotation;

import java.lang.annotation.*;

/**
 * 可以訪問的角色
 * @Author su
 * @Date 2021/3/11 17:26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Access {
    RolesEnum[] value();
}
