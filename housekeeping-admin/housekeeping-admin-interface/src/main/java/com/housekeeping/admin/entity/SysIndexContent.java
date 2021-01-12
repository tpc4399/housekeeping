package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元素&工作内容 中间表
 * @Author su
 * @Date 2021/1/12 14:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_index_content")
public class SysIndexContent extends Model<SysIndexContent> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;    /* 主鍵id */
    private Integer indexId;   /* 元素_id */
    private Integer contentId;  /* 工作内容(一级标签)_id */

}
