package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.entity.EmployeesDetails;
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
}
