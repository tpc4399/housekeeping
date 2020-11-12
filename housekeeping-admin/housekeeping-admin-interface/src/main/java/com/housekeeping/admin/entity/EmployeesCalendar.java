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
 * 员工日程表，表明在这个时间段是可以进行排班的
 * @Author su
 * @create 2020/11/12 15:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_calendar")
public class EmployeesCalendar extends Model<EmployeesCalendar> {

    /* 主鍵id */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /* 计量标准 */
    private Boolean stander;

    /* 日期 */
    private LocalDate data;

    /* 周数 */
    private String week;

    /* 员工_id */
    private Integer employeesId;

    /* 时间段开始 */
    private LocalTime timeSlotStart;

    /* 时间段长度(小时) */
    private Float timeSlotLength;

}
