package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/19 14:37
 */
@Data
public class UpdateEmployeesCalendarDTO {

    private Integer id;
    private List<Integer> weeks;
    private LocalTime timeSlotStart;            /* 時間段開始 */
    private Float timeSlotLength;               /* 時間段長度 */
    private Float price;                        /* 時薪 */
    private String code;                        /* 貨幣代碼 */

}
