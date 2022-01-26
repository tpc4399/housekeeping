package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeesSkillDTO {
    private Integer employeesId;
    private List<SkillAndPriceDTO> skills;
}
