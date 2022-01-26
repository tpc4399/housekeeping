package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/16 14:36
 */
@Data
public class RulesDateVo {
    private LocalDate date;     //日期
    private List<TimeSlot> timeSlotVos;   //日期的多个时间段
}
