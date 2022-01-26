package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 需求单详情
 * @Author su
 * @Date 2021/3/2 16:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demand_order_details")
@NoArgsConstructor
@AllArgsConstructor
public class DemandOrderDetails extends Model<DemandOrderDetails> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                         /* 主键id */
    private Integer demandOrderId;              /* 需求单id */
    private LocalDate date;                     /* 日期 */
    private LocalTime start;                    /* 开始时间段 */
    private Float length;                       /* 时间段长度 */

}
