package com.housekeeping.admin.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2021/5/9 10:25
 */
@Data
public class TimeSlotPOJO {

    private LocalTime timeSlotStart;            /* 時間段開始 */
    private Float timeSlotLength;               /* 時間段長度 */
    private Integer type;           /* 收費類型 0固定金額 1百分比 */
    private Integer percentage;     /* 百分比金額 */
    private BigDecimal hourlyWage;              /* 時薪 */
    private String code;                        /* 貨幣代碼 */

    public TimeSlotPOJO() {
    }

    public TimeSlotPOJO(TimeSlotPOJO timeSlotPOJO) {
        this.timeSlotStart = timeSlotPOJO.getTimeSlotStart();
        this.timeSlotLength = timeSlotPOJO.getTimeSlotLength();
        this.type = timeSlotPOJO.getType();
        this.percentage = timeSlotPOJO.getPercentage();
        this.hourlyWage = timeSlotPOJO.getHourlyWage();
        this.code = timeSlotPOJO.getCode();
    }
}
