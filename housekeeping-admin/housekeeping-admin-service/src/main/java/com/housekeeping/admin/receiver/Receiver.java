package com.housekeeping.admin.receiver;

import com.housekeeping.admin.entity.Log;
import com.housekeeping.admin.service.ILogService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author su
 * @create 2020/10/31 18:11
 */

@Component
@RabbitListener(
    bindings=@QueueBinding(
        value=@Queue(value="${mq.info.queue}",autoDelete="false"),
        exchange=@Exchange(value="${mq.exchange}",type= ExchangeTypes.DIRECT),
        key="${mq.info.routerKey}"
    )
)
public class Receiver {

    @Resource
    private ILogService logService;

    @RabbitHandler
    public void process(Log log){
        logService.addLog(log);
    }
}
