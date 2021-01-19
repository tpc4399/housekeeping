package com.housekeeping.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author su
 * @Date 2021/1/19 10:50
 */
@Data
@AllArgsConstructor
public class PeriodOfTimeSuper {
    private PeriodOfTime periodOfTime;
    private PeriodOfTimeWithHourlyWage periodOfTimeWithHourlyWage;
}
