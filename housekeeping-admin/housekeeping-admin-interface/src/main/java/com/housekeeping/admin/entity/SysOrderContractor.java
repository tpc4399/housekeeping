package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oder_contractor")
public class SysOrderContractor extends Model<SysOrderContractor> {

    @TableId(value = "id",type = IdType.AUTO)
    public Integer id;                      /* 主键id */
    private Integer orderId;                /* 订单id */
    public LocalDate startTime;             /* 开始时间 */
    public LocalDate forwardTime;        /* 期望完成时间 */
}

