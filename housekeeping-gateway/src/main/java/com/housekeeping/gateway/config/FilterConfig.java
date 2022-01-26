package com.housekeeping.gateway.config;

import com.housekeeping.gateway.filter.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author su
 * @create 2020/11/4 23:34
 */
@Configuration
public class FilterConfig {
    @Bean
    LoginFilter loginFilter(){
        return new LoginFilter();
    }
}
