package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/27 17:29
 */
@Data
public class JobsDTO {

    private Integer jobId;
    private List<TimeSlotAndPriceDTO> priceSlot;

}
