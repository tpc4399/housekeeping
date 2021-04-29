package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.EmployeesWorkExperience;
import lombok.Data;

import java.util.List;

@Data
public class EmployeesDetailsWorkVo {

    private EmployeesDetails employeesDetails;
    private List<EmployeesWorkExperience> employeesWorkExperiences;
}
