package com.housekeeping.admin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsYearVO {

    private String month;
    private Integer orderTotal;
    private BigDecimal priceTotal;
}
