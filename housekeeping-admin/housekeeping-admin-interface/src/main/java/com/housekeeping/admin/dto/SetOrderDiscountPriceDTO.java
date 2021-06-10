package com.housekeeping.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author su
 * @create 2021/6/10 15:38
 */
@Data
public class SetOrderDiscountPriceDTO {

    private String number;                        //订单编号
    private BigDecimal discountPrice;                 //折后价

}
