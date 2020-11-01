package com.housekeeping.common.logs.aspect;

import com.housekeeping.admin.entity.Log;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.logs.utils.LogUtils;
import com.housekeeping.common.rabbitmq.utils.Sender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/10/30 14:56
 */
@Aspect
@Slf4j
public class LogAspect {

    @Resource
    private Sender sender;

    @Around("@annotation(logFlag)")
    @SneakyThrows
    public Object around(ProceedingJoinPoint point, LogFlag logFlag) {
        String strClassName = point.getTarget().getClass().getName();
        String strMethodName = point.getSignature().getName();
        log.debug("[类名]:{},[方法]:{}", strClassName, strMethodName);

        Log log = LogUtils.getSysLog();
        log.setTitle(logFlag.description());
        // 发送异步日志事件
        Long startTime = System.currentTimeMillis();
        Object obj = point.proceed();
        Long endTime = System.currentTimeMillis();
        log.setTime(endTime - startTime);
        log.setCreateTime(LocalDateTime.now());
        log.setDelFlag(0);
        System.out.println("主任务用时："+(endTime - startTime));

        Long startTime1 = System.currentTimeMillis();
        //发送消息
        sender.sendMessageInfo(log);
        Long endTime1 = System.currentTimeMillis();
        System.out.println("异步任务(发送mq消息)用时："+(endTime1 - startTime1));

        return obj;
    }
}
