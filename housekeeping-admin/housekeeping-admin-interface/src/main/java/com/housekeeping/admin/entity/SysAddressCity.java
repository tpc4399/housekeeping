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
@TableName("sys_address_city")
public class SysAddressCity extends Model<SysAddressCity> {

    private Integer id;
    private String name;

}
