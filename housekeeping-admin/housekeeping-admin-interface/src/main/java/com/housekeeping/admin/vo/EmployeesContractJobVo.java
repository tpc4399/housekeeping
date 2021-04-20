package com.housekeeping.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class EmployeesContractJobVo {

    private Integer id;

    private String name;

    private Boolean status;

    private List<ContractJobVo> contents;
}
