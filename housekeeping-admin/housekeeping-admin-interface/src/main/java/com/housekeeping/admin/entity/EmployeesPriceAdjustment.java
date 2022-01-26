package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_price_adjustment")
public class EmployeesPriceAdjustment extends Model<EmployeesPriceAdjustment> {

    @TableId(type= IdType.AUTO)
    private Integer id;             //主键id

    private Integer employeesId;    //员工id

    private String startDate;       //开始日期

    private String date;            //日期

    private String endDate;         //结束日期

    private String jobIds;          //工作类型

    private Integer type;           //收費類型 0固定金額 1百分比

    private Integer percentage;     //百分比时薪

    private BigDecimal hourlyWage; //固定時薪

    private String code;            //貨幣代碼

    private Boolean status;         //收费开关
}
