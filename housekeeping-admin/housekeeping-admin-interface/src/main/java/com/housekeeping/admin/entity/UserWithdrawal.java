package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_withdrawal")
public class UserWithdrawal extends Model<UserWithdrawal> {

    @TableId(type= IdType.AUTO)
    private Integer id;

    private Integer userId;

    private BigDecimal amount;      //提现金额

    private String bank;

    private String collectionAccount;

    private LocalDateTime createTime;

    private Integer status;         //0提现申请中 1提现拒绝 2提现同意 3提现已发放


}
