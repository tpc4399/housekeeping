package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/25 14:51
 */
public interface IGroupManagerService extends IService<GroupManager> {
    R add(GroupManagerDTO groupManagerDTO);

    R delete(GroupManagerDTO groupManagerDTO);

    R getAllMan(GroupDTO groupDTO);

    R getAllManById(Integer groupId);
}
