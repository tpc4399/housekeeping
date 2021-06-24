package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class WorkTimeTableVO {
    private Integer id;
    private LocalTime timeSlots;
    private Float timeLength;
    private BigDecimal timePrice;
    private Boolean canBeOnDuty;
    private BigDecimal todayPrice;
    private OrderDetailsPOJO orderDetails;
    private Integer workStatus;
    private Integer toWorkStatus;
    private LocalDateTime toWorkTime;
    private Integer offWorkStatus;
    private LocalDateTime offWorkTime;
}
