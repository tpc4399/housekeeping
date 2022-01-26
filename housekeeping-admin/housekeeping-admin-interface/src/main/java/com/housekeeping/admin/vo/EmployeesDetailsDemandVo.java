package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmployeesDetailsDemandVo {

    private Integer id;
    private EmployeesDetails employeesDetails;
    private DemandOrder demandOrder;
    private List<WorkDetailsPOJO> workDetailsPOJOS;
    private Integer price;
    private Integer status;
    private LocalDateTime createTime;
    private List<EmployeesDetails> employeesDetailsList;
}
