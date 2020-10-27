package com.housekeeping.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author su
 * @create 2020/10/27 1:05
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class HousekeepingAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(HousekeepingAuthApplication.class, args);
    }
}
