package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/2/4 19:01
 */
@Data
public class ContractAndPriceDetails {

    private EmployeesContract contract;     /* 包工 */
    private BigDecimal totalPrice;          /* 总價格 */
    private Float attendance;               /* 总出勤率 */
    private Map<LocalDate, List<TimeSlot>> noAttendance;  /* 不能出勤的详细时间 */

    public ContractAndPriceDetails() {
    }

    public ContractAndPriceDetails(EmployeesContract contract, BigDecimal totalPrice, Float attendance, Map<LocalDate, List<TimeSlot>> noAttendance) {
        this.contract = contract;
        this.totalPrice = totalPrice;
        this.attendance = attendance;
        this.noAttendance = noAttendance;
    }
}
