package com.housekeeping.admin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsWeekVO {

    private String startDate;
    private String endDate;
    private Integer orderTotal;
    private BigDecimal priceTotal;
}
