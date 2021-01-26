package com.housekeeping.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/26 15:15
 */
@Data
@AllArgsConstructor
public class PriceSlotVo {
    private BigDecimal lowPrice;
    private BigDecimal highPrice;

    public PriceSlotVo(){}
}
