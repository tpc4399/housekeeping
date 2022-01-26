package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.Skill;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class QuotationVo {

    private Integer id;
    private EmployeesDetails employeesDetails;
    private DemandOrder demandOrder;
    private List<WorkDetailsPOJO> workDetailsPOJOS;
    private BigDecimal price;
    private Integer status;
    private Integer days;
    private Float h;
    private Integer certified;
    private List<Skill> workContent;
    private List<Skill> workType;
    private List<EmployeesDetails> employeesDetailsList;
}
