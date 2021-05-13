package com.housekeeping.admin.pojo;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/15 9:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkDetailsPOJO {
    private LocalDate date;             //日期
    private Integer week;               //星期几
    private List<TimeSlot> timeSlots;   //安排时间段
    private Boolean canBeOnDuty;        //能否出勤
    private BigDecimal todayPrice;      //今日价格 台币
}
