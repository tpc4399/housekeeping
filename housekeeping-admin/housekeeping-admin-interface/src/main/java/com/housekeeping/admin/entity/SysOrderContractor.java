package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oder_contractor")
public class SysOrderContractor extends Model<SysOrderContractor> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;                     /* 主键id */
    private Integer orderId;                /* 订单id */
    private Integer jobContendId;           /* 工作内容_id */
    private LocalDateTime startTime;        /* 预期开始时间 */
    private LocalDateTime endTime;          /* 期望完成时间 */
}

