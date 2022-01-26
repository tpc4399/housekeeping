package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 外汇币种代码
 * @Author su
 * @Date 2020/12/15 16:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_foreign_currency")
public class SysForeignCurrency extends Model<SysForeignCurrency> {
}
