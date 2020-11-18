package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @create 2020/11/17 21:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_address")
public class CustomerAddress extends Model<CustomerAddress> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;             /* 主键id */
    private Integer customerId;     /* 顾客_id */
    private Boolean isDefault;      /* 是否默认地址 */
    private String name;            /* 地址名 */
    private String address;         /* 详细地址 */
}
