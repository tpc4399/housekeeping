package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2021/5/28 16:29
 */
@Data
public class SetWorkDetailsDTO {

    private LocalDate date;             //日期
    private Integer week;               //周数
    private List<TimeSlot> timeSlots;   //安排时间段
    private BigDecimal todayPrice;      //今日价格 台币

}
