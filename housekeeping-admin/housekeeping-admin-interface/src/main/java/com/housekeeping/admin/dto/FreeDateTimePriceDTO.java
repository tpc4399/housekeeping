package com.housekeeping.admin.dto;

import com.housekeeping.admin.pojo.TimeSlotPOJO;
import com.housekeeping.admin.pojo.TimeSlotPricePOJO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FreeDateTimePriceDTO {
    private Integer week;               //今天周几
    private LocalDate date;
    private List<TimeSlotPricePOJO> times;
    private Boolean hasTime;       //今天是否有時間工作

    public FreeDateTimePriceDTO(FreeDateTimePriceDTO freeDateTimePriceDTO) {
        this.week = freeDateTimePriceDTO.getWeek();
        this.date = freeDateTimePriceDTO.getDate();
        this.times = freeDateTimePriceDTO.getTimes();
        this.hasTime = freeDateTimePriceDTO.getHasTime();
    }

    public FreeDateTimePriceDTO() {
    }
}
