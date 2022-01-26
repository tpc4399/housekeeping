package com.housekeeping.common.rabbitmq.utils;

import com.housekeeping.admin.entity.Log;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author su
 * @create 2020/10/31 17:39
 */
@Component("sender")
@Async
public class Sender {

    @Value("${mq.exchange}")
    private String exchange;

    @Value("${mq.info.routerKey}")
    private String routerKeyInfo;

    @Value("${mq.error.routerKey}")
    private String routerKeyError;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessageInfo(Log msg){
        amqpTemplate.convertAndSend(exchange, routerKeyInfo, msg);
    }
    public void sendMessageError(Log msg){
        amqpTemplate.convertAndSend(exchange, routerKeyError, msg);
    }

}
