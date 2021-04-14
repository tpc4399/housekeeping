package com.housekeeping.order;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author su
 * @Date 2021/4/14 10:45
 */
@Order(3)
@EnableAsync
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableEurekaClient
@EnableCircuitBreaker
@EnableFeignClients
@EnableTransactionManagement
public class HousekeepingOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(HousekeepingOrderApplication.class, args);
    }
}
