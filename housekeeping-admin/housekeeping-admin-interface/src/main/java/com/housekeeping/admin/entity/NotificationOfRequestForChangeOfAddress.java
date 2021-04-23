package com.housekeeping.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2021/4/23 10:30
 */
@Data
public class NotificationOfRequestForChangeOfAddress {

    private Integer id;                  //主键
    private Long number;                 //订单编号
    private String name;
    private String phone;
    private String phPrefix;
    private String address;
    private Float lat;
    private Float lng;
    private LocalDateTime requestDateTime; //请求修改的时间
    private Boolean result;                 //保洁员的处理结果，true同意 false拒绝

}
