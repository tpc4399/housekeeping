package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/8 22:19
 */
@Data
public class SetEmployeesJobsDTO {

    private List<Integer> jobIds;
    private Integer employeesId;

}
