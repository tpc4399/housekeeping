package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/30 13:23
 */
@Data
public class SetEmployeesCalendarWeekDTO {

    private Integer employeesId;            /* 保潔員_id */
    private List<Integer> week;             /* 週數 */
    private List<TimeSlotDTO> timeSlotList; /* 時間段s */

}
