package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/8 22:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetEmployeesJobsDTO {

    private List<Integer> jobIds;
    private Integer employeesId;

}
