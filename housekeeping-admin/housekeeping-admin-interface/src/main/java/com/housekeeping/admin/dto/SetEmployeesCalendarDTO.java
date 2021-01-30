package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/29 17:03
 */
@Data
public class SetEmployeesCalendarDTO {

    private Integer employeesId;            /* 保潔員_id */
    private List<TimeSlotDTO> timeSlotList; /* 時間段s */

}
