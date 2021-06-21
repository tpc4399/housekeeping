package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.entity.ManagerDetails;
import lombok.Data;

import java.util.List;

@Data
public class GroupDetailsVo extends GroupDetails {

    private String noCertifiedCompany;

    private List<EmployeesDetailsSkillVo> employeesDetails;

    private List<ManagerDetails> managerDetails;
}
