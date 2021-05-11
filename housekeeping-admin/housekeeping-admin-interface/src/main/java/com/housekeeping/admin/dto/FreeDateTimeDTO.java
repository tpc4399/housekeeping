package com.housekeeping.admin.dto;

import com.housekeeping.admin.pojo.TimeSlotPOJO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FreeDateTimeDTO {

    private Integer week;               //今天周几

    private LocalDate date;

    private List<TimeSlotPOJO> times;

    private Boolean hasTime;
}
