package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/28 17:27
 */
@Data
public class SetCalendarAllDTO {

    private List<Integer> week;    /* 週數 */
    private List<TimeSlotPriceDTO> timeSlotPriceDTOList;

}
