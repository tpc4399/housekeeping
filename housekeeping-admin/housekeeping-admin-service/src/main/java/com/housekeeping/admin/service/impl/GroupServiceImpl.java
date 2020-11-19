package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.mapper.GroupMapper;
import com.housekeeping.admin.service.GroupService;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@Service("groupService")
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDetails> implements GroupService {


    @Override
    public R saveGroup(Map map) {
        if(CollectionUtils.isNotEmpty(map)){
            String groupName = (String)map.get("groupName");
            Integer groupManagerId = (Integer)map.get("groupManagerId");
            ArrayList<Integer> groupEmployeesIds  = (ArrayList)map.get("groupEmployeesIds");
            for (int i = 0; i < groupEmployeesIds.size(); i++) {
                GroupDetails groupDetails = new GroupDetails();
                groupDetails.setGroupName(groupName);
                groupDetails.setGroupManagerId(groupManagerId);
                groupDetails.setGroupEmployeesIds(groupEmployeesIds.get(i));
                groupDetails.setCreateTime(LocalDateTime.now());
                groupDetails.setUpdateTime(LocalDateTime.now());
                groupDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                this.save(groupDetails);
            }
        }
        return R.ok("添加分組成功");
    }

    @Override
    public R cusRemove(Integer groupManagerId) {
        return baseMapper.cusRemove(groupManagerId);
    }
}
