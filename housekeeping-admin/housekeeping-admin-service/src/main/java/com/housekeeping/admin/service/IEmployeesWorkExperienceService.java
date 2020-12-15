package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.EmployeesWorkExperienceDTO;
import com.housekeeping.admin.entity.EmployeesWorkExperience;
import com.housekeeping.common.utils.R;

import java.util.List;


/**
 * @Author su
 * @Date 2020/12/2 9:50
 */
public interface IEmployeesWorkExperienceService extends IService<EmployeesWorkExperience> {

    void saveEmployeesWorkExperience(List<EmployeesWorkExperienceDTO> employeesWorkExperienceDTOS,
                                     Integer employeesId);

    void updateEmployeesWorkExperience(List<EmployeesWorkExperienceDTO> employeesWorkExperienceDTOS,
                                     Integer employeesId);

    void deleteByEmployeesId(Integer employeesId);

    R getAll(Integer employeesId);
}
