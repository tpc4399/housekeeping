package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;         /* 主键 */
    private Long number;        /* 订单编号 */
    private LocalDate date;     /* 日期 */
    private Integer week;       /* 星期几 */
    private String timeSlots;   /* 时段 */
    private Boolean canBeOnDuty;        //能否出勤
    private BigDecimal todayPrice;      //今日价格

}
