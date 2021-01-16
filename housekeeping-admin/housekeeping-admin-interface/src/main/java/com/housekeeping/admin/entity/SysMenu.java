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
@TableName("sys_menu")
public class SysMenu extends Model<SysMenu> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;         /* 主鍵id */
    private String name;        /* 菜單名 */
    private String icon;        /* 图标 */
    private String url;         /* url */
    private String type;        /* 参数1 */

}
