package com.housekeeping.admin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2020/11/12 17:14
 */
@Data
public class TimeSlotVo extends TimeSlot{

    private BigDecimal hourlyWage; /* 时薪 */

    private String code; /* 时薪货币代码 */
}
