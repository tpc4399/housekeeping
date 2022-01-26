package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/11 10:40
 */
@Data
public class EmployeesJobsDTO {
    private Integer employeesId;
    private List<Integer> jobIds;
}
