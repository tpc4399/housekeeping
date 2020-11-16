package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/16 14:35
 */
@Data
public class RulesWeekVo {
    private LocalDate start;
    private LocalDate end;
    private String week;
    private List<TimeSlotVo> timeSlotVos;
}
