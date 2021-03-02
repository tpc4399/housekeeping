package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/25 14:51
 */
public interface IGroupManagerService extends IService<GroupManager> {
    R save(GroupManagerDTO groupManagerDTO);

    R getAllMan(GroupDTO groupDTO);

    R getAllManById(Integer groupId);

    R getAllManByAdmin(GroupAdminDTO groupDTO);

    R saveByAdmin(GroupManagerAdminDTO groupManagerDTO);
}
