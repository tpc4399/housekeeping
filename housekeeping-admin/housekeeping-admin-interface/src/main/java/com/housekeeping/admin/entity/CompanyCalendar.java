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
 * 公司时间表模板
 * @Author su
 * @create 2021/5/31 8:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_calendar")
public class CompanyCalendar extends Model<CompanyCalendar> {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;    /* 主鍵id */
    private Integer companyId;    /* 公司_id */
    private Boolean stander;    /* 计量标准 */
    private LocalDate date;    /* 日期 */
    private String week;    /* 周数 */
    private LocalTime timeSlotStart;    /* 时间段开始 */
    private Float timeSlotLength;    /* 时间段长度(小时) */
    private Integer type;           /* 收費類型 0固定金額 1百分比 */
    private Integer percentage;     /* 百分比金額 */
    private BigDecimal hourlyWage;  /* 固定金額時薪 */
    private String code;            /* 貨幣代碼 */

    public CompanyCalendar() {
    }

    public CompanyCalendar(Integer companyId,
                             Boolean stander,
                             LocalDate data,
                             String week,
                             LocalTime timeSlotStart,
                             Float timeSlotLength) {
        this.companyId = companyId;
        this.stander = stander;
        this.date = data;
        this.week = week;
        this.timeSlotStart = timeSlotStart;
        this.timeSlotLength = timeSlotLength;
    }

}
