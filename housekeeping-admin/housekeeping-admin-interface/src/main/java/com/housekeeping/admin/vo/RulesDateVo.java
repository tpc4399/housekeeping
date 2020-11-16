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
    private LocalDate date;
    private List<TimeSlotVo> timeSlotVos;
}
