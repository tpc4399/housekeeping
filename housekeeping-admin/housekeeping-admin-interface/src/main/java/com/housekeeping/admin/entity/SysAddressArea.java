package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/3/8 16:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_address_area")
public class SysAddressArea extends Model<SysAddressArea> {

    private Integer id;
    private Integer parentId;
    private String name;

}
