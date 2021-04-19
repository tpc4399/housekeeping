package com.housekeeping.admin.pojo;

import com.housekeeping.admin.vo.TimeSlot;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/15 9:36
 */
public class WorkDetailsPOJO {
    private LocalDate date;             //日期
    private String week;                //星期几
    private List<TimeSlot> timeSlots;   //安排时间段
}
