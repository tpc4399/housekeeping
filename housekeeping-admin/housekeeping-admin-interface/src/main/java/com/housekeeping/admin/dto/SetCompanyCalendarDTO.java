package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/31 9:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetCompanyCalendarDTO {

    private Integer companyId;
    private List<Integer> week;
    private List<TimeSlotPriceDTO> timeSlotPriceDTOList;

}
