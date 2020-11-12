package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlotVo;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/12 17:09
 */
@Data
public class EmployeesCalendarDTO {

    /* 员工 */
    private Integer employeesId;

    /* 时间段 */
    private List<TimeSlotVo> timeSlots;

}
