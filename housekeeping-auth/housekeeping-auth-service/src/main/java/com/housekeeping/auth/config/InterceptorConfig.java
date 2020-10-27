package com.housekeeping.auth.config;

import com.housekeeping.auth.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 * @Author su
 * @create 2020/10/26 22:55
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public AuthenticationInterceptor authenticationInterceptor(){
        return new AuthenticationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        /***
         * 拦截所有请求
         */
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/**");
    }
}
