package com.housekeeping.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author su
 * @Date 2021/2/4 13:47
 */
@Data
public class Attendance {
    private ReadWriteLock rwl = new ReentrantReadWriteLock();

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
        rwl.writeLock().lock();
        try {
            this.enableTotalHourly += 0.5f;
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void increaseTheTotalPrice(BigDecimal value){
        rwl.writeLock().lock();
        try {
            this.totalPrice = this.totalPrice.add(value);
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
