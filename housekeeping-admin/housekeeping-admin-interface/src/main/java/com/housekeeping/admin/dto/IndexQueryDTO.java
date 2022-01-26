package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @Date 2021/1/12 15:08
 */
@Data
public class IndexQueryDTO {
    private Integer indexId; /* 元素_id */
    private LocalDate date;  /* 服务日期 */
    private List<TimeSlot> timeSlotList; /* 上门时间段 */
    private String code; /* 货币单位代码 */
    private String lowestPrice; /* 最低价 */
    private String highestPrice; /* 最高价 */
    private Integer addressId;   /* 服务地址_id */
}
