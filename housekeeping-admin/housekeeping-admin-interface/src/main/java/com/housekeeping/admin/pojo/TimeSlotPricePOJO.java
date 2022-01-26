package com.housekeeping.admin.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2021/5/9 10:25
 */
@Data
public class TimeSlotPricePOJO {

    private LocalTime timeSlotStart;            /* 時間段開始 */
    private Float timeSlotLength;               /* 時間段長度 */
    private BigDecimal hourlyWage;              /* 時薪 */
    private String code;                        /* 貨幣代碼 */
    private BigDecimal totalPrice;              /* 时薪：工作内容小时价+时间附加价 */

    public TimeSlotPricePOJO(TimeSlotPricePOJO timeSlotPricePOJO) {
        this.timeSlotStart = timeSlotPricePOJO.getTimeSlotStart();
        this.timeSlotLength = timeSlotPricePOJO.getTimeSlotLength();
        this.hourlyWage = timeSlotPricePOJO.getHourlyWage();
        this.code = timeSlotPricePOJO.getCode();
        this.totalPrice = timeSlotPricePOJO.getTotalPrice();
    }

    public TimeSlotPricePOJO() {
    }
}
