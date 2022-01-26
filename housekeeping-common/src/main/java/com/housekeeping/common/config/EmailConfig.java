package com.housekeeping.common.config;

import com.housekeeping.common.utils.EmailUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author su
 * @create 2020/11/10 12:03
 */
@Configuration
public class EmailConfig {

    @Bean
    public EmailUtils emailUtils(){
        return new EmailUtils();
    }

}
