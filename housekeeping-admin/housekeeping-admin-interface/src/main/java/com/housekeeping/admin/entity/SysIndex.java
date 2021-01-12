package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/1/12 14:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_index")
public class SysIndex extends Model<SysIndex> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;    /* 主鍵id */
    private String name;   /* 元素名字 */

}
