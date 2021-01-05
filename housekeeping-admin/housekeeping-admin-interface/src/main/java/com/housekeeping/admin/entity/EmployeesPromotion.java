package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_promotion")
public class EmployeesPromotion extends Model<EmployeesPromotion> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;             /* 主键id */
    private Integer employeesId;      /* 員工id */
    private Boolean promotion;      /* 是否推广 默认0未推广 1已推广 */
    private LocalDateTime startTime;/* 开始时间 */
    private LocalDateTime endTime;  /* 结束时间 */
    private Integer days;          /* 推广天數 */
}
