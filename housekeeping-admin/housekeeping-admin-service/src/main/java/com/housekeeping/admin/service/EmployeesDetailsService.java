package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.common.utils.R;

public interface EmployeesDetailsService extends IService<EmployeesDetails> {

    R saveEmp(EmployeesDetails employeesDetails);

}
