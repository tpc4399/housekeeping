package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
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

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;    /* 主鍵id */

    private Boolean stander;    /* 计量标准 */

    private LocalDate data;    /* 日期 */

    private String week;    /* 周数 */

    private Integer employeesId;    /* 员工_id */

    private LocalTime timeSlotStart;    /* 时间段开始 */

    private Float timeSlotLength;    /* 时间段长度(小时) */

    private BigDecimal hourlyWage; /* 时薪 */

    private String code; /* 时薪货币代码 */

}
