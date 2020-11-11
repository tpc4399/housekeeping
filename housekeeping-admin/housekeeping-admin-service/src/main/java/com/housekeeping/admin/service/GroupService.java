package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.Group;

import java.util.Map;

public interface GroupService extends IService<Group> {

    R saveGroup(Map map);

    R cusRemove(Integer groupManagerId);
}
