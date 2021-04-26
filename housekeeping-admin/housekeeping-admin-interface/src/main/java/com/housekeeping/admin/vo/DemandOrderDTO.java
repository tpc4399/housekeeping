package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.DemandOrder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class DemandOrderDTO extends DemandOrder {

    private List<TimeSlot> timeSlots;
}


