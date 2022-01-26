package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.EmployeesPriceAdjustment;
import com.housekeeping.admin.entity.Skill;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EmployeesPriceAdjustmentDTO  {

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

    private Boolean status;

    private List<Skill> skillTags;

    public EmployeesPriceAdjustmentDTO(EmployeesPriceAdjustment employeesPriceAdjustment) {
        this.id = employeesPriceAdjustment.getId();
        this.employeesId = employeesPriceAdjustment.getEmployeesId();
        this.startDate = employeesPriceAdjustment.getStartDate();
        this.date = employeesPriceAdjustment.getDate();
        this.endDate = employeesPriceAdjustment.getEndDate();
        this.jobIds = employeesPriceAdjustment.getJobIds();
        this.type = employeesPriceAdjustment.getType();
        this.percentage = employeesPriceAdjustment.getPercentage();
        this.hourlyWage = employeesPriceAdjustment.getHourlyWage();
        this.code = employeesPriceAdjustment.getCode();
        this.status = employeesPriceAdjustment.getStatus();
    }

    public EmployeesPriceAdjustmentDTO() {
    }
}
