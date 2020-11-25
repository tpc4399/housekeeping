package com.housekeeping.admin.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @create 2020/11/25 14:23
 */
@Data

public class GroupManagerDTO {

    private Integer groupId;        /* 组_id */
    private Integer managerId;    /* 经理_id */

}
