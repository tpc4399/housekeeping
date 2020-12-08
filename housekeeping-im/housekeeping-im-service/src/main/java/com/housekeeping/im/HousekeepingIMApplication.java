package com.housekeeping.im;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.Order;

@Order(4)
@SpringBootApplication
@EnableCaching
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.housekeeping.im")
public class HousekeepingIMApplication {

    public static void main(String[] args) {
        SpringApplication.run(HousekeepingIMApplication.class, args);
    }

}
