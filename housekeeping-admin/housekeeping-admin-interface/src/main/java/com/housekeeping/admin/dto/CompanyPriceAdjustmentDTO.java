package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.CompanyPriceAdjustment;
import com.housekeeping.admin.entity.Skill;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CompanyPriceAdjustmentDTO {

    private Integer id;             //主键id

    private Integer companyId;    //员工id

    private String startDate;       //开始日期

    private String date;            //日期

    private String endDate;         //结束日期

    private String jobIds;          //工作类型

    private Integer type;           //收費類型 0固定金額 1百分比

    private Integer percentage;     //百分比时薪

    private BigDecimal hourlyWage; //固定時薪

    private String code;            //貨幣代碼

    private List<Skill> skillTags;

    public CompanyPriceAdjustmentDTO(CompanyPriceAdjustment companyPriceAdjustment) {
        this.id = companyPriceAdjustment.getId();
        this.companyId = companyPriceAdjustment.getCompanyId();
        this.startDate = companyPriceAdjustment.getStartDate();
        this.date = companyPriceAdjustment.getDate();
        this.endDate = companyPriceAdjustment.getEndDate();
        this.jobIds = companyPriceAdjustment.getJobIds();
        this.type = companyPriceAdjustment.getType();
        this.percentage = companyPriceAdjustment.getPercentage();
        this.hourlyWage = companyPriceAdjustment.getHourlyWage();
        this.code = companyPriceAdjustment.getCode();
    }

    public CompanyPriceAdjustmentDTO() {
    }
}
