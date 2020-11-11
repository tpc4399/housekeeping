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
@TableName("group_details")
public class Group extends Model<Group> {

    private static final long serialVersionUID = 1L;

    /* 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /* 組名 */
    private String groupName;

    /* 組所屬經理id */
    private Integer groupManagerId;

    /* 組包括員工id */
    private Integer groupEmployeesIds;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer lastReviserId;
}
