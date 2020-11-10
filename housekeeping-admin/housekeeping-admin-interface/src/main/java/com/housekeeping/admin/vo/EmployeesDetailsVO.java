package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import lombok.Data;

@Data
public class EmployeesDetailsVO extends EmployeesDetails {

    private String lastReviserName;

    private String companyName;
}
