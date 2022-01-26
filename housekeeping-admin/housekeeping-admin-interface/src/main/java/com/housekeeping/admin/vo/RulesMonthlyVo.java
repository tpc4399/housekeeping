package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/16 15:31
 */
@Data
public class RulesMonthlyVo {
    private LocalDate start;
    private LocalDate end;
    private List<TimeSlot> timeSlotVoWorkingDays; /* 工作日的時間段 */
    private List<TimeSlot> timeSlotVoHolidayDays; /* 節假日的時間段 */
}
