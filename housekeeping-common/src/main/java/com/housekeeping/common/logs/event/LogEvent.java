package com.housekeeping.common.logs.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author su
 * @create 2020/10/30 15:07
 */
public class LogEvent extends ApplicationEvent {
    public LogEvent(Object source) {
        super(source);
    }
}
