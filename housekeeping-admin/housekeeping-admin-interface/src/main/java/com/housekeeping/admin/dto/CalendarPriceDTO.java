package com.housekeeping.admin.dto;

import com.housekeeping.admin.pojo.TimeSlotPOJO;
import com.housekeeping.admin.pojo.TimeSlotPricePOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2021/5/13 15:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarPriceDTO {

    private Integer week;               //今天周几
    private LocalDate date;
    private List<TimeSlotPricePOJO> times;
    private Boolean hasTime;       //今天是否有時間工作
    private Boolean isThisMonth;   //今天是否是本月的
    private Boolean isAfter;

    public CalendarPriceDTO(FreeDateTimePriceDTO dto) {
        this.week = dto.getWeek();
        this.date = dto.getDate();
        this.times = dto.getTimes();
        this.hasTime = dto.getHasTime();
    }
}
