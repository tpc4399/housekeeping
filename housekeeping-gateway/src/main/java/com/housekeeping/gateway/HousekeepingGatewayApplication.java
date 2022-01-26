package com.housekeeping.gateway;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Order(10)
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableDiscoveryClient
@EnableZuulProxy
@EnableFeignClients
@ComponentScan("com.housekeeping.gateway.config")
@EnableSwagger2
public class HousekeepingGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(HousekeepingGatewayApplication.class, args);
    }
}
