package com.housekeeping.admin.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * @Author su
 * @Date 2020/12/25 17:15
 */
@Data
@NoArgsConstructor
public class TimeSlot {
    /* 时间段开始点 */
    private LocalTime timeSlotStart;
    /* 时间段长度（h） */
    private Float timeSlotLength;

    public TimeSlot(LocalTime timeSlotStart, Float timeSlotLength) {
        Float rw = new Float(24);
        Float h = Float.valueOf(timeSlotStart.getHour());
        Float l = h+timeSlotLength;
        this.timeSlotStart = timeSlotStart;
        if (l > rw) this.timeSlotLength = rw - h;
        else this.timeSlotLength = timeSlotLength;
    }
}
