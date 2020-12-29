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
 * 客户发布订单中的详细计划
 * @Author su
 * @create 2020/11/16 11:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order_plan")
public class SysOrderPlan extends Model<SysOrderPlan> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                     /* 主键id */
    private Integer orderId;                /* 订单id */
    private Boolean type;                   /* 类型：true钟点工 false包工 */
    private LocalDate date;                 /* 钟点工生效，日期 */
    private LocalTime timeSlotStart;        /* 钟点工生效，时间段开始 */
    private Float timeSlotLength;           /* 钟点工生效，时间段长度(小时) */
    private Integer jobContendId;           /* 包工生效，工作内容 */
    private LocalDateTime startTime;        /* 包工生效，开始时间 */

}
