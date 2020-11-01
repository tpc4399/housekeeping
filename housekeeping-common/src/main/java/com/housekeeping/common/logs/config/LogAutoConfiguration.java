package com.housekeeping.common.logs.config;

import com.housekeeping.common.logs.aspect.LogAspect;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author su
 * @create 2020/10/26 22:31
 */
@EnableAsync
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication
public class LogAutoConfiguration {
    @Bean
    public LogAspect logAspect() {
        return new LogAspect();
    }
}
