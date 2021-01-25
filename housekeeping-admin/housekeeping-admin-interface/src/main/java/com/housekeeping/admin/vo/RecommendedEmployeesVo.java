package com.housekeeping.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/21 17:47
 */
@Data
@AllArgsConstructor
@ToString
public class RecommendedEmployeesVo {
    private Integer employeesId;
    private Integer instance;
    private BigDecimal price;
    private Float score;
}
