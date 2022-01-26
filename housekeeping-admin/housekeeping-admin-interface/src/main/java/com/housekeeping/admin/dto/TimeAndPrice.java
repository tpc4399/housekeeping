package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author su
 * @Date 2021/2/4 13:53
 */
@Data
public class TimeAndPrice {

    private LocalTime time;            /* 時間段 */
    private List<JobAndPriceDTO> jobAndPriceList;/*可工作內容（價格，貨幣代碼）s*/

    public TimeAndPrice() {
    }

    public TimeAndPrice(LocalTime time, List<JobAndPriceDTO> jobAndPriceList) {
        this.time = time;
        this.jobAndPriceList = jobAndPriceList;
    }
}
