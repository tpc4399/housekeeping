package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.common.utils.R;

import javax.servlet.http.HttpServletRequest;

public interface EmployeesDetailsService extends IService<EmployeesDetails> {
    R saveEmp(EmployeesDetails employeesDetails);

    R updateEmp(EmployeesDetails employeesDetails);

    IPage cusPage(Page page, Integer id);

    R getLinkToLogin(Integer id, Long h, HttpServletRequest request);
}
