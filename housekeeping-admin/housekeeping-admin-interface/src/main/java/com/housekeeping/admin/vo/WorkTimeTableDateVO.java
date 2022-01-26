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

    private LocalDate date;         //当前日期
    private Integer week;           //星期
    private Boolean isThisMonth;    //是否是当前月份
    private Boolean isThisDay;      //是否是当天
    private Boolean hasWork;        //当天是否有工作
    private Boolean isAfter;
    private List<WorkTimeTableVO> workTimeTable;        //工作详情
}
