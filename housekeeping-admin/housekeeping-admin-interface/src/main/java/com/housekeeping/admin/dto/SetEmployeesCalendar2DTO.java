package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/8 17:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetEmployeesCalendar2DTO {

    private Integer employeesId;   /* 保潔員_id */
    private List<Integer> week;    /* 週數 */
    private List<TimeSlotPriceDTO> timeSlotPriceDTOList;

    @Override
    public String toString() {
        return "SetEmployeesCalendar2DTO{" +
                "employeesId=" + employeesId +
                ", week=" + week +
                ", timeSlotPriceDTOList=" + timeSlotPriceDTOList +
                '}';
    }
}
