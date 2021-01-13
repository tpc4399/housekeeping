package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/1/13 17:09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("manager_menu")
public class ManagerMenu extends Model<ManagerMenu> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;         /* 主鍵id */
    private Integer managerId;  /* 經理id */
    private Integer menuId;     /* 菜單id */

}
