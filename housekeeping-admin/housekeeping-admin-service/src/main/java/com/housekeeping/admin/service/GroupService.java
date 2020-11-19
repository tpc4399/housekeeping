package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.Group;

public interface GroupService extends IService<Group> {

    R cusRemove(Integer id);

    IPage getGroup(Page page, Integer companyId, Integer id);

    R addMan(Integer groupId, Integer managerId);
}
