package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_index")
public class Index extends Model<Index> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;         /* 主鍵id */
    private String name;        /* 分類名稱 */

}
