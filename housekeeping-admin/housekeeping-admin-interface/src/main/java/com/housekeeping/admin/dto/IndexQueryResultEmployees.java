package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.EmployeesDetails;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 被匹配的保洁员
 * @Author su
 * @Date 2021/2/4 14:51
 */
@Data
public class IndexQueryResultEmployees {
    private EmployeesDetails employeesDetails;          /* 保洁员信息 */
    private Integer employeesType;                      /* 保洁员类型 1、只有钟点工匹配ok  2、只有包工匹配ok  3、钟点工和包工都匹配ok */
    private List<JobAndPriceDetails> service1;          /* 匹配到的钟点工信息 */
    private List<ContractAndPriceDetails> service2;     /* 匹配到的包工信息 */
    private Double instance;                            /* 保洁员的距离 m */
    private Float score;                                /* 保洁员的评分 */
    private Integer recommendedValue;                   /* 推荐值 */
    private Double recommendedScope;                    /* 推荐分数 */
    private BigDecimal price;                           /* 价格 */
    private Boolean certified;                          /* 是否已認證 */
}
