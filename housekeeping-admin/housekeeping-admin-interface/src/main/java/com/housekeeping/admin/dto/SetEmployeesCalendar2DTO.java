package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/8 17:57
 */
@Data
public class SetEmployeesCalendar2DTO {

    private Integer employeesId;   /* 保潔員_id */
    private List<Integer> week;    /* 週數 */
    private List<TimeSlotPriceDTO> timeSlotPriceDTOList;

}
