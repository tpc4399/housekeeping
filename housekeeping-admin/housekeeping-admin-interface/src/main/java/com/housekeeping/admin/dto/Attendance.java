package com.housekeeping.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/2/4 13:47
 */
@Data
public class Attendance {
    private Integer jobId;              /* 工作内容一级标签_id */
    private Float enableTotalHourly;    /* 可出勤时长 */
    private BigDecimal totalPrice;      /* 总价格 */

    public Attendance() {
    }

    public Attendance(Integer jobId, Float enableTotalHourly, BigDecimal totalPrice) {
        this.jobId = jobId;
        this.enableTotalHourly = enableTotalHourly;
        this.totalPrice = totalPrice;
    }

    public void halfAnHourMore(){
        this.enableTotalHourly += 0.5f;
    }

    public void increaseTheTotalPrice(BigDecimal value){
        this.totalPrice = this.totalPrice.add(value);
    }
}
