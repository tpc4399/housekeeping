package com.housekeeping.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * redis延时队列的消息封装
 * @Author su
 * @create 2021/6/9 16:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     * 消息唯一标识
     */
    private String id;
    /**
     * 消息渠道 如 订单 支付 代表不同业务类型
     * 为消费时不同类去处理
     */
    private String channel;
    /**
     * 具体消息 json
     */
    private String body;

    /**
     * 延时时间 被消费时间  取当前时间戳 延迟时间 毫秒
     */
    private Long delayTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

