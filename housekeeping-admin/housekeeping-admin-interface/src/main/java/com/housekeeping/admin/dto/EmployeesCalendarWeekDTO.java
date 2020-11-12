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
public class EmployeesCalendarWeekDTO {

    /* 员工 */
    private Integer employeesId;

    /* 星期 */
    private List<Integer> weeks;

    /* 时间段 */
    private List<TimeSlotVo> timeSlots;

}
