package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.EmployeesDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/3/29 17:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeesInstanceDTO {
    private EmployeesDetails employeesDetails;
    private Double instance;
}
