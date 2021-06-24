package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.OrderDetails;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class WorkTimeTableDateVO {

    private LocalDate date;
    private Integer week;
    private List<WorkTimeTableVO> workTimeTable;
}
