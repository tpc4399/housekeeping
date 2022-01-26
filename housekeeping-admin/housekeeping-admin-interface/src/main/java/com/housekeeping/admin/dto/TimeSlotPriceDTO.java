package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalTime;

/**
 * @Author su
 * @Date 2021/4/8 17:59
 */
@Data
public class TimeSlotPriceDTO {

    private LocalTime timeSlotStart;            /* 時間段開始 */
    private Float timeSlotLength;               /* 時間段長度 */
    private Integer type;           /* 收費類型 0固定金額 1百分比 */
    private Integer percentage;     /* 百分比金額 */
    private Float price;                        /* 時薪 */
    private String code;                        /* 貨幣代碼 */

}
