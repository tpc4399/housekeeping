package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 顾客需求计划
 * @Author su
 * @create 2020/11/13 15:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order")
public class CustomerDemandPlan extends Model<CustomerDemandPlan> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;    /* 主键id */
    private Integer orderId; /* 订单_id */
    private Integer employeesId; /* 保洁员_id */
    private LocalDate date; /* 日期 */
    private LocalTime timeSlotStart;    /* 开始时间段 */
    private Float timeSlotLength;/* 时间段长度（h） */
    private String work;/* 计划工作任务 */
    private BigDecimal hourlyWage; /* 时薪 */
    private String code; /* 时薪货币代码 */

}
