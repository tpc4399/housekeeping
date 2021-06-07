package com.housekeeping.common.entity;

import java.time.LocalTime;

/**
 * @Author su
 * @create 2021/6/7 15:36
 */
public class TimeSlotWithPrice {

    /* 时间段开始点 */
    private LocalTime timeSlotStart;
    /* 时间段长度（h） */
    private Float timeSlotLength;
    /* 该时间段价格 */
    private String price;

    public TimeSlotWithPrice(LocalTime timeSlotStart, Float timeSlotLength) {
        Float rw = new Float(24);
        Float h = Float.valueOf(timeSlotStart.getHour());
        Float l = h+timeSlotLength;
        this.timeSlotStart = timeSlotStart;
        if (l > rw) this.timeSlotLength = rw - h;
        else this.timeSlotLength = timeSlotLength;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "timeSlotStart=" + timeSlotStart +
                ", timeSlotLength=" + timeSlotLength +
                '}';
    }

}
