package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Author su
 * @Date 2021/4/21 10:41
 */
@Data
public class AppointmentContractDTO {

    private Integer contractId;//包工服务id
    private Integer addressId; //地址id
    private LocalDate startDate; //开始日期
    private LocalTime startTime; //开始时间

}
