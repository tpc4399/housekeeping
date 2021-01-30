package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/1/29 16:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_calendar_details")
public class EmployeesCalendarDetails extends Model<EmployeesCalendarDetails> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;             /* 主鍵_id */
    private Integer calendarId;     /* 時間表記錄_id */
    private Integer jobId;          /* 工作內容_id */
    private Float price;            /* 價格 */
    private String code;            /* 貨幣代碼 */

    public EmployeesCalendarDetails() {
    }

    public EmployeesCalendarDetails(Integer calendarId, Integer jobId, Float price, String code) {
        this.calendarId = calendarId;
        this.jobId = jobId;
        this.price = price;
        this.code = code;
    }
}
