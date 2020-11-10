package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeesDetailsVO extends EmployeesDetails {

    private String lastReviserName;

    private String companyName;
}
