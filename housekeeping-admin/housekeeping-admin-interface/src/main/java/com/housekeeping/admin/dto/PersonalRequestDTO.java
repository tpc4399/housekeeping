package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.PersonalRequest;
import lombok.Data;

@Data
public class PersonalRequestDTO extends PersonalRequest {

    private EmployeesDetails employeesDetails;
}
