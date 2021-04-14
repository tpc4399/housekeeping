package com.housekeeping.register;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.core.annotation.Order;

@Order(0)
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableEurekaServer
public class HousekeepingRegisterApplication {
    public static void main(String[] args) {
        SpringApplication.run(HousekeepingRegisterApplication.class, args);
    }
}