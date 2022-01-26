package com.housekeeping.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/27 17:31
 */
@Data
public class TimeSlotAndPriceDTO {

    private Float lowH;
    private Float highH;
    private BigDecimal price;

}
