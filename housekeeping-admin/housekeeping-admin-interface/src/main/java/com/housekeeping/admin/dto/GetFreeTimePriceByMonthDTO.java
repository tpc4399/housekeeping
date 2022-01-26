package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @create 2021/5/13 15:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFreeTimePriceByMonthDTO {

    private Integer employeesId; //保洁员id
    private Integer year;        //年份
    private Integer month;       //月份
    private Integer jobId;       //工作内容id

}
