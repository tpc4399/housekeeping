package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompanySkillsDTO {

    private Integer id;
    private Integer companyId;
    private List<SkillDTO> skills;
}
