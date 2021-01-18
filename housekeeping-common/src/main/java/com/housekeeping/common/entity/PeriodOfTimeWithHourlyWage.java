package com.housekeeping.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @Author su
 * @Date 2021/1/18 10:48
 */
@Data
@AllArgsConstructor
public class PeriodOfTimeWithHourlyWage {
    private LocalTime timeSlotStart;    /* 时间段开始点 */
    private Float timeSlotLength;       /* 时间段长度（h） */
    private BigDecimal hourlyWage;      /* 时薪 */
}
