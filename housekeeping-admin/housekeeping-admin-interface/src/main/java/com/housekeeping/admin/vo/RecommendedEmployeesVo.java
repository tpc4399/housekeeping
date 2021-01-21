package com.housekeeping.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/21 17:47
 */
@Data
@AllArgsConstructor
public class RecommendedEmployeesVo {
    private Integer employeesId;
    private Integer instance;
    private BigDecimal price;
    private Float score;
}
