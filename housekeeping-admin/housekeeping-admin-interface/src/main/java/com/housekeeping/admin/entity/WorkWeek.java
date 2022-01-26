package com.housekeeping.admin.entity;

import lombok.Data;

@Data
public class WorkWeek {
    private String startDate;
    private String endDate;
    private Integer workTotal;
}
