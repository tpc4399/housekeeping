package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @Date 2021/1/30 13:23
 */
@Data
public class SetEmployeesCalendarDateDTO {

    private Integer employeesId;            /* 保潔員_id */
    private LocalDate date;                 /* 日期 */
    private List<TimeSlotDTO> timeSlotList; /* 時間段s */

}
