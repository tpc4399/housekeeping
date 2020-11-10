package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.common.utils.R;

import java.net.UnknownHostException;

public interface ManagerDetailsService extends IService<ManagerDetails> {
    R saveEmp(ManagerDetails managerDetails);

    R updateEmp(ManagerDetails managerDetails);

    IPage cusPage(Page page, Integer id);

    R getLinkToLogin(Integer id, Long h) throws UnknownHostException;

}
