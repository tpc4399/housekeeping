package com.housekeeping.common.config;

import com.housekeeping.common.entity.ConversionRatio;
import com.housekeeping.common.utils.MongoUtils;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;

/**
 * @Author su
 * @Date 2021/2/28 13:09
 */
@Configuration
public class OthersBeanConfig {

    @Lazy
    @Bean("conversionRatio")
    public ConversionRatio conversionRatio() {
        //TODO: find auth info from RPC context, http header, cookie, and validate it
        return new ConversionRatio();
    }

    @Bean
    public MongoUtils getMongoUtils(){
        return new MongoUtils();
    }

}
