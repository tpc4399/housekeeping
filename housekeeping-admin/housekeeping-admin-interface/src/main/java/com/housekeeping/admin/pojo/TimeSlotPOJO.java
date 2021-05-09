package com.housekeeping.admin.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2021/5/9 10:25
 */
@Data
public class TimeSlotPOJO {

    private LocalTime timeSlotStart;            /* 時間段開始 */
    private Float timeSlotLength;               /* 時間段長度 */
    private BigDecimal hourlyWage;              /* 時薪 */
    private String code;                        /* 貨幣代碼 */
}
