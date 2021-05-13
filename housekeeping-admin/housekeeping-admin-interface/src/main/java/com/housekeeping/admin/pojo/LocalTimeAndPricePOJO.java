package com.housekeeping.admin.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2021/5/13 14:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalTimeAndPricePOJO {
    private LocalTime time;                     /* 时间点 */
    private BigDecimal hourlyWage;              /* 時薪 */
    private String code;                        /* 貨幣代碼 */
}
