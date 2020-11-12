package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
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

    /* 主鍵id */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /* 员工_id */
    private Integer employeesId;

    /* 日期 */
    private LocalDate data;

    /* 时间段开始 */
    private LocalTime timeSlotStart;

    /* 时间段长度(小时) */
    private Float timeSlotLength;

    /* 时薪标准，是否默认值 */
    private Boolean hourlySalaryStandard;

    /* 时薪 */
    private String hourlyWage;

    /* 时薪单位代码，币种代码 */
    private String code;

    /* 是否法定节假日 */
    private Boolean isLegalHolidays;

    /* 是否请假 */
    private Boolean isLeave;

    /* 任务_id */
    private Integer taskId;
}
