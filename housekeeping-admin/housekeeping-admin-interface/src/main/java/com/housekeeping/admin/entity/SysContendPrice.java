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
 * @Date 2020/12/11 16:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_contend_price")
public class SysContendPrice extends Model<SysContendPrice> {

    @TableId(type = IdType.AUTO)
    private Integer id;         /* 主鍵id */
    private Integer contendId;
    private Integer flat;
    private Integer hour;
    private Integer companyPrice;
    private Integer personalPrice;
    private Boolean status;

}
