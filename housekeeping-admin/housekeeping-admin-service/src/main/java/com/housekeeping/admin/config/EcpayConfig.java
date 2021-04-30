package com.housekeeping.admin.config;

import ecpay.payment.integration.AllInOne;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author su
 * @Date 2021/4/30 10:25
 */
@Configuration
public class EcpayConfig {

    @Bean
    public AllInOne allInOne(){
        return new AllInOne("");
    }

}
