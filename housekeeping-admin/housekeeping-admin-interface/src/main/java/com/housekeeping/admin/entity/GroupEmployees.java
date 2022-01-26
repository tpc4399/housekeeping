package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @create 2020/11/19 14:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("group_employees")
@AllArgsConstructor
@NoArgsConstructor
public class GroupEmployees extends Model<GroupEmployees> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;             /* 主键_id */
    private Integer groupId;        /* 组_id */
    private Integer employeesId;    /* 员工_id */

}
