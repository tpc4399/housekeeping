package com.housekeeping.common.config;

import com.housekeeping.common.entity.ConversionRatio;
import org.springframework.context.annotation.*;

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

}
