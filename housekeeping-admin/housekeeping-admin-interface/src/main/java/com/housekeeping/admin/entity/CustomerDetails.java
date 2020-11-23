package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/23 10:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_details")
public class CustomerDetails extends Model<CustomerDetails> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                /* 主键 */
    private Integer userId;         /* 用户_id */
    private String number;          /* 编号 */
    private String name;            /* 名字 */
    private String phonePrefix;     /* 手机号前缀 */
    private String phone;           /* 手机号 */
    private String email;           /* 邮箱 */
    private String address;         /* 地址 */
    private Integer numberOfReservations;/* 预约单数量 */
    private Integer numberOfDemand; /* 需求单数量 */
    private Boolean sex;            /* 性别 */
    private String photoUrl;        /* 照片_url */
    private Boolean blacklistFlag;  /* 是否加入黑名单 */
    private LocalDateTime createTime;/* 创建时间 */
    private LocalDateTime updateTime;/* 更新时间 */
    private Integer lastReviserId;  /* 最后修改人 */

}
