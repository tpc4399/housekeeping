package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.GroupDetails;

import java.util.Map;

public interface GroupService extends IService<GroupDetails> {

    R saveGroup(Map map);

    R cusRemove(Integer groupManagerId);
}
