package com.housekeeping.admin.pojo;

import com.housekeeping.admin.entity.EmployeesCalendar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Author su
 * @create 2021/5/2 21:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarPOJO {

    private Integer id;    /* 主鍵id */
    private Integer employeesId;    /* 员工_id */
    private Boolean stander;    /* 计量标准 */
    private LocalDate date;    /* 日期 */
    private String week;    /* 周数 */
    private LocalTime timeSlotStart;    /* 时间段开始 */
    private Float timeSlotLength;    /* 时间段长度(小时) */
    private Float price;            /* 價格 */
    private String code;            /* 貨幣代碼 */

    public CalendarPOJO(EmployeesCalendar calendar) {
        this.id = calendar.getId();
        this.employeesId = calendar.getEmployeesId();
        this.stander = calendar.getStander();
        this.date = calendar.getDate();
        this.week = calendar.getWeek();
        this.timeSlotStart = calendar.getTimeSlotStart();
        this.timeSlotLength = calendar.getTimeSlotLength();
    }
}
