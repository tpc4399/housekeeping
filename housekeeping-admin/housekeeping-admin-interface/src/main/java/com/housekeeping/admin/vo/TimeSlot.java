package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalTime;

/**
 * @Author su
 * @Date 2020/12/25 17:15
 */
@Data
public class TimeSlot {

    /* 时间段开始点 */
    private LocalTime timeSlotStart;
    /* 时间段长度（h） */
    private Float timeSlotLength;

}
