package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalTime;

/**
 * @Author su
 * @create 2020/11/12 17:14
 */
@Data
public class TimeSlotVo {
    /* 时间段开始点 */
    private LocalTime timeSlotStart;
    /* 时间段长度（h） */
    private Float timeSlotLength;
}
