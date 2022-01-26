package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约包工的参数
 * @Author su
 * @Date 2021/4/15 9:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action2DTO {

    private LocalDate startDate;    /* 开始日期 */
    private LocalTime startTime;    /* 开始时间 */

}
