package com.housekeeping.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * @Author su
 * @Date 2021/4/15 9:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotVo {

    private LocalTime start;
    private Float length;

}
