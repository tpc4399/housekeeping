package com.housekeeping.common.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2020/11/15 3:16
 */
@Data
public class PeriodOfTime {
    private LocalTime timeSlotStart;    /* 时间段开始点 */
    private Float timeSlotLength;    /* 时间段长度（h） */
}
