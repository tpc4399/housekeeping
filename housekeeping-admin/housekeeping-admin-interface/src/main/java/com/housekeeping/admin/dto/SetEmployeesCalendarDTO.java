package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/29 17:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetEmployeesCalendarDTO {

    private Integer employeesId;            /* 保潔員_id */
    private List<TimeSlotDTO> timeSlotList; /* 時間段s */

}
