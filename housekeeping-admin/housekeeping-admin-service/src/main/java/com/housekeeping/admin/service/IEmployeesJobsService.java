package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.EmployeesJobsDTO;
import com.housekeeping.admin.entity.EmployeesJobs;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @Date 2021/1/11 10:22
 */
public interface IEmployeesJobsService extends IService<EmployeesJobs> {
    R updateEmployeesJobs(EmployeesJobsDTO employeesJobsDTO);
}
