package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 员工时间表_计划    实体
 * @Author su
 * @create 2020/11/12 15:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_worksheet_plan")
public class EmployeesWorksheetPlan extends Model<EmployeesWorksheetPlan> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;                     /* 主鍵id */
    private Integer employeesId;            /* 员工_id */
    private LocalDate data;                 /* 日期 */
    private LocalTime timeSlotStart;        /* 时间段开始 */
    private Float timeSlotLength;           /* 时间段长度(小时) */
    private Boolean hourlySalaryStandard;   /* 时薪标准，是否默认值 */
    private String hourlyWage;              /* 时薪 */
    private String code;                    /* 时薪单位代码，币种代码 */
    private Boolean isLegalHolidays;        /* 是否法定节假日 */
    private Boolean isLeave;                /* 是否请假 */
    private Integer taskId;                 /* 任务_id */
    private Boolean isPerform;              /* 是否执行 */
    private LocalDateTime byTheTime;        /* 顾客确认截止时间 */
}
