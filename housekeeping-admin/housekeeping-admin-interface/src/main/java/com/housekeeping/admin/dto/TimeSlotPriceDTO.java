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
    private Float price;                        /* 時薪 */
    private String code;                        /* 貨幣代碼 */

}
