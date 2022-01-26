package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/19 14:37
 */
@Data
public class UpdateCompanyCalendarDTO {

    private Integer id;
    private List<Integer> weeks;
    private LocalTime timeSlotStart;            /* 時間段開始 */
    private Float timeSlotLength;               /* 時間段長度 */
    private Integer type;           /* 收費類型 0固定金額 1百分比 */
    private Integer percentage;     /* 百分比金額 */
    private Float price;                        /* 時薪 */
    private String code;                        /* 貨幣代碼 */

}
