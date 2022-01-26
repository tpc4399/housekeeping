package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 员工工作记录 实体
 * @Author su
 * @create 2020/11/12 15:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_task_record")
public class EmployeesTaskRecord extends Model<EmployeesTaskRecord> {

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

    /* 时薪 */
    private String hourlyWage;

    /* 时薪单位代码，币种代码 */
    private String code;

    /* 任务_id */
    private Integer taskId;

}
