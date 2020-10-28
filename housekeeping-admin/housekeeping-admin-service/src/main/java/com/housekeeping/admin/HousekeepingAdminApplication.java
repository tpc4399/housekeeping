package com.housekeeping.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.Order;

@Order(2)
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class HousekeepingAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(HousekeepingAdminApplication.class, args);
    }
}
