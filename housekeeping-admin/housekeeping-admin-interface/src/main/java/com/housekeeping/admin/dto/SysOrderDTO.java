package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/18 14:02
 */
@Data
public class SysOrderDTO {

    private String number; /* 订单编号 */
    private Integer companyId; /* 公司_id */
    private Integer customerId;/* 客户_id */
    private Integer addressId;/* 服务地址_id */
    private Boolean type; /* 订单类型：false = 需求单;true = 预约单 */
    private LocalDateTime createTimeStart; /* 订单创建时间 */
    private LocalDateTime createTimeEnd; /* 订单创建时间 */
    private Float totalTimeMin; /* 订单服务总时长 */
    private Float totalTimeMax; /* 订单服务总时长 */
}
