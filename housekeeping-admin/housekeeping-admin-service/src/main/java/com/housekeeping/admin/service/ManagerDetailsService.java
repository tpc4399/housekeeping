package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.ManagerDetailsDTO;
import com.housekeeping.admin.dto.PageOfManagerDTO;
import com.housekeeping.admin.dto.PageOfManagerDetailsDTO;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.common.utils.R;

import java.net.UnknownHostException;

public interface ManagerDetailsService extends IService<ManagerDetails> {
    R saveEmp(ManagerDetailsDTO managerDetailsDTO);

    R updateEmp(ManagerDetailsDTO managerDetailsDTO);

    R getLinkToLogin(Integer id, Long h) throws UnknownHostException;

    Integer getCompanyIdByManagerId(Integer managerId);

    R cusPage1(Page page, PageOfManagerDTO pageOfEmployeesDTO, String type);

    R cusPage(Page page, PageOfManagerDetailsDTO pageOfEmployeesDetailsDTO, String type);
}
