package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.Skill;
import lombok.Data;

import java.util.List;

@Data
public class EmployeesDetailsSkillVo extends EmployeesDetails {

    private List<Skill> skillTags;

    private Integer certified;

    private String noCertifiedCompany;

    private  Integer collectionVolume;
}
