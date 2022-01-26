package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/19 14:52
 */
@Data
public class GroupEmployeesDTO {

    private Integer groupId;        /* 组_id */
    private List<Integer> employeesId;    /* 员工_id */

}
