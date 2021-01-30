package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author su
 * @Date 2021/1/29 17:12
 */
@Data
public class TimeSlotDTO {

    private LocalTime timeSlotStart;            /* 時間段開始 */
    private Float timeSlotLength;               /* 時間段長度 */
    private List<JobAndPriceDTO> jobAndPriceList;/*可工作內容（價格，貨幣代碼）s*/

}
