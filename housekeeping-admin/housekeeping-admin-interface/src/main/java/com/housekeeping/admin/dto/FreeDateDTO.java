package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FreeDateDTO {

    private LocalDate date;

    private List<TimeSlotDTO> times;

    private Boolean hasTime;
}
