package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * @Author su
 * @Date 2021/2/1 12:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_contract_details")
public class EmployeesContractDetails extends Model<EmployeesContractDetails> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;                 /* 主键 */
    private Integer contractId;         /* 包工_id */
    private String week;                /* 周数 */
    private LocalTime timeSlotStart;    /* 时间段开始 */
    private Float timeSlotLength;       /* 时间段长度 */

}
