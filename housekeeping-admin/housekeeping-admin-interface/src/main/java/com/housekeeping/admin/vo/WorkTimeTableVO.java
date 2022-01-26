package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class WorkTimeTableVO {
    private Integer id;                 //工作id
    private String  workProgress;       //工作进度
    private LocalTime timeSlots;        //开始时间
    private Float timeLength;           //时间长度
    private BigDecimal timePrice;       //时间价格
    private Boolean canBeOnDuty;        //能否出勤
    private BigDecimal todayPrice;      //天价格（没用）
    private OrderDetailsPOJO orderDetails;  //
    private Integer workStatus;         //工作状态
    private Integer toWorkStatus;       //上班打卡状态（0未打卡 1已打卡）
    private LocalDateTime toWorkTime;   //上班打卡时间
    private Integer offWorkStatus;      //下班打卡状态
    private LocalDateTime offWorkTime;  //下班打卡时间
}
