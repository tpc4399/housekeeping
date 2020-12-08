package com.housekeeping.im.entity;

import lombok.Data;

@Data
public class SendInfo {

    /**
     * 发送信息的代码
     */
    private String code;

    /**
     * 信息
     */
    private Message message;


}
