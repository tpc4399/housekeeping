package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.Skill;
import lombok.Data;

import java.util.List;

@Data
public class DemandEmployeesStatusVo {

    private EmployeesDetails    employeesDetails;
    private Boolean status;         /* 参与报价 */
    private List<Skill> skills;
}
