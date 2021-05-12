package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.CustomerAddress;
import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.entity.Skill;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class DemandOrderDTO extends DemandOrder {

    private List<TimeSlot> timeSlots;

    private CustomerAddress customerAddress;

    private List<Skill> workContent;

    private List<Skill> workType;
}


