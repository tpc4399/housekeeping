package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private LocalDate date; /* 日期 */
    private String week;    /* 星期几 */
    private LocalTime timeSlotStart;    /* 开始时间段 */
    private Float timeSlotLength;/* 时间段长度（h） */
    private Boolean type;/* 计划类型:null=每天;false=指定日期;true=按周 */
    private LocalDateTime start;/* 计划开始日期 */
    private LocalDateTime end;/* 计划结束日期 */
    private Float totalTime;/* 计划总时长 */
    private String work;/* 计划工作任务 */

}
