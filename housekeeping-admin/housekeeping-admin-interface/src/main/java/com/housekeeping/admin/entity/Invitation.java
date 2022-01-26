package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("invitation")
public class Invitation extends Model<Invitation> {

    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer invitee;    //邀请人userId
    private Integer invitees;   //被邀请人userId
    private BigDecimal bonus;      //佣金（台币）
}
