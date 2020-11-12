package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlotVo;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/12 17:09
 */
@Data
public class EmployeesCalendarDateDTO {

    /* 员工 */
    private Integer employeesId;

    /* 日期 */
    private List<LocalDate> date;

    /* 时间段 */
    private List<TimeSlotVo> timeSlots;

}
