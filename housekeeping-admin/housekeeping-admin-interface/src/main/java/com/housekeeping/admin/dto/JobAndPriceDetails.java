package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/2/4 14:54
 */
@Data
public class JobAndPriceDetails {
    private Integer jobId;      /* 工作內容_id */
    private BigDecimal totalPrice;   /* 总價格 */
    private Float attendance;   /* 总出勤率 */
    private Map<LocalDate, List<TimeSlot>> noAttendance;  /* 不能出勤的详细时间 */

    public JobAndPriceDetails() {
    }

    public JobAndPriceDetails(Integer jobId, BigDecimal totalPrice, Float attendance, Map<LocalDate, List<TimeSlot>> noAttendance) {
        this.jobId = jobId;
        this.totalPrice = totalPrice;
        this.attendance = attendance;
        this.noAttendance = noAttendance;
    }
}
