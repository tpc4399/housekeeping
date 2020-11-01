package com.housekeeping.common.rabbitmq.config;

import com.housekeeping.common.rabbitmq.utils.Sender;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author su
 * @create 2020/10/31 17:31
 */
@Configuration
public class RabbitConfig {
    @Bean
    public Queue helloQueue(){
        return new Queue("hello");
    }
    @Bean
    public Sender sender(){
        return new Sender();
    }
}
