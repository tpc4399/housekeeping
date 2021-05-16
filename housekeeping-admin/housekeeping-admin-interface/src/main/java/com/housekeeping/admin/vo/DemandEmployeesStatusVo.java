package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import lombok.Data;

@Data
public class DemandEmployeesStatusVo {

    private EmployeesDetails    employeesDetails;
    private Boolean status;         /* 参与报价 */
}
