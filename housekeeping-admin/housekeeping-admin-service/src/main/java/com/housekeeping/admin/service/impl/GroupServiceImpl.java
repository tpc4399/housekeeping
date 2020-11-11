package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.Group;
import com.housekeeping.admin.mapper.GroupMapper;
import com.housekeeping.admin.service.GroupService;
import com.housekeeping.common.utils.TokenUtils;
import org.omg.CORBA.INTERNAL;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Service("groupService")
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {


    @Override
    public R saveGroup(Map map) {
        if(CollectionUtils.isNotEmpty(map)){
            String groupName = (String)map.get("groupName");
            Integer groupManagerId = (Integer)map.get("groupManagerId");
            ArrayList<Integer> groupEmployeesIds  = (ArrayList)map.get("groupEmployeesIds");
            for (int i = 0; i < groupEmployeesIds.size(); i++) {
                Group group = new Group();
                group.setGroupName(groupName);
                group.setGroupManagerId(groupManagerId);
                group.setGroupEmployeesIds(groupEmployeesIds.get(i));
                group.setCreateTime(LocalDateTime.now());
                group.setUpdateTime(LocalDateTime.now());
                group.setLastReviserId(TokenUtils.getCurrentUserId());
                this.save(group);
            }
        }
        return R.ok("添加分組成功");
    }

    @Override
    public R cusRemove(Integer groupManagerId) {
        return baseMapper.cusRemove(groupManagerId);
    }
}
