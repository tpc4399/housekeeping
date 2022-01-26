package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CompanySkillsDTO;
import com.housekeeping.admin.entity.CompanySkills;
import com.housekeeping.admin.entity.Invitation;
import com.housekeeping.common.utils.R;

public interface CompanySkillsService extends IService<CompanySkills> {

    R saveCompanySkills(CompanySkillsDTO companySkillsDTO);

    R cusUpdate(CompanySkillsDTO companySkillsDTO);

    R getCompanySkills(Integer id, Integer companyId);

    R copyByEmp(Integer id, Integer empId);
}
