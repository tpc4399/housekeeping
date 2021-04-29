package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author su
 * @Date 2021/4/20 15:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_details")
@AllArgsConstructor
@NoArgsConstructor
public class WorkDetails extends Model<WorkDetails> {

    private Integer id;         /* 主键 */
    private Long number;        /* 订单编号 */
    private LocalDate date;     /* 日期 */
    private Integer week;       /* 星期几 */
    private String timeSlots;   /* 时段 */

}
