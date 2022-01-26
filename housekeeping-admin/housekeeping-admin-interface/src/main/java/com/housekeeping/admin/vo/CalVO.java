package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CalVO {

    private LocalDate date;
    private Boolean isThisMonth;
}
