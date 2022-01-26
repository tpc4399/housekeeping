package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.SysJobContend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private Integer certified;
    private List<SysJobContend> jobs;
}
