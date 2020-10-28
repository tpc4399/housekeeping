package com.housekeeping.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.Order;

/**
 * @Author su
 * @create 2020/10/27 1:05
 */
@Order(2)
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class HousekeepingAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(HousekeepingAuthApplication.class, args);
    }
}
