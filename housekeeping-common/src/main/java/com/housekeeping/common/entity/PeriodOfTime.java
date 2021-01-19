package com.housekeeping.common.entity;

import com.housekeeping.common.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2020/11/15 3:16
 */
@Data
@AllArgsConstructor
public class PeriodOfTime {
    private LocalTime timeSlotStart;    /* 时间段开始点 */
    private Float timeSlotLength;    /* 时间段长度（h） */

    public PeriodOfTime() {
    }

    @Override
    public String toString() {
        return "PeriodOfTime(" + timeSlotStart + "+" + timeSlotLength + "h)";
    }

    public static PeriodOfTime getIntersection(PeriodOfTime a, PeriodOfTime b){
        return CommonUtils.getIntersectionTimeSlot(a, b);
    }
}
