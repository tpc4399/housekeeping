package com.housekeeping.admin.config;

import com.housekeeping.admin.auth.aspect.AccessAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author su
 * @Date 2021/3/11 18:06
 */
@Configuration
@ConditionalOnWebApplication
public class AccessAutoConfig {

    @Bean
    public AccessAspect accessAspect(){
        return new AccessAspect();
    }

}
