package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/2/22 16:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("company_scale")
public class CompanyScale extends Model<CompanyScale> {

    private Integer id;              /* 主键不自增 */
    private String scale;            /* 人数范围 */
    private BigDecimal monthPrice;   /* 月价格 */
    private BigDecimal yearPrice;    /* 年价格 */
    private String code;             /* 价格单位货币代码 */

}
