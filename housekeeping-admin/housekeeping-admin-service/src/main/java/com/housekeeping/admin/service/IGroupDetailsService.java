package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.dto.GroupDetailsUpdateDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.common.utils.R;

public interface IGroupDetailsService extends IService<GroupDetails> {

    R saveGroup(GroupDetailsDTO groupDetailsDTO);

    R updateGroup(GroupDetailsUpdateDTO groupDetailsUpdateDTO);

    R cusRemove(Integer id);

    IPage getGroup(Page page, Integer companyId, Integer id);

}
