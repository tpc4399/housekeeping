package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author su
 * @Date 2021/2/3 11:19
 */
@Data
public class DateSlot {

    private LocalDate start;    /* 开始日期 */
    private LocalDate end;      /* 结束日期 */

}
