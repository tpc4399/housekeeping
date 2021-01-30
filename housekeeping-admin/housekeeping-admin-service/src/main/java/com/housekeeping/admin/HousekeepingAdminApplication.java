package com.housekeeping.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Order(2)
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableTransactionManagement
public class HousekeepingAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(HousekeepingAdminApplication.class, args);
    }
}
