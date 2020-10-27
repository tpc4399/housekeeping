package com.housekeeping.auth.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author su
 * @create 2020/10/27 2:19
 */
@Configuration
@MapperScan("com.housekeeping.auth.mapper")
public class MybatisConfig {
}
