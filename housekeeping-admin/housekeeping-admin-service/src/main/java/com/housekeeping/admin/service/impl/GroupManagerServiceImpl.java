package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.admin.mapper.GroupManagerMapper;
import com.housekeeping.admin.service.IGroupManagerService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @create 2020/11/25 14:52
 */
@Service("groupManagerService")
public class GroupManagerServiceImpl extends ServiceImpl<GroupManagerMapper, GroupManager> implements IGroupManagerService {
    @Override
    public R add(GroupManagerDTO groupManagerDTO) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        groupManagerDTO.getManagerId().forEach(x -> {
            GroupManager groupManager = new GroupManager();
            groupManager.setManagerId(x);
            groupManager.setGroupId(groupManagerDTO.getGroupId());
            //判断是否存在
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("group_id", groupManagerDTO.getGroupId());
            queryWrapper.eq("manager_id", x);
            List<GroupManager> res = baseMapper.selectList(queryWrapper);
            if ( res == null || res.size() == 0){
                baseMapper.insert(groupManager);
                count.getAndSet(count.get() + 1);
            }
        });
        Integer s = groupManagerDTO.getManagerId().size() - count.get();
        if (s == 0){
            return R.ok("分組成功添加"+count.get()+"個经理");
        }else {
            return R.ok("分組成功添加"+count.get()+"個经理,有" + s + "个经理是已经存在于该分组的");
        }
    }

    @Override
    public R delete(GroupManagerDTO groupManagerDTO) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        groupManagerDTO.getManagerId().forEach(x -> {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("group_id", groupManagerDTO.getGroupId());
            queryWrapper.eq("manager_id", x);
            baseMapper.delete(queryWrapper);
            count.getAndSet(count.get() + 1);
        });
        return R.ok("分組成功删除"+count.get()+"個经理");
    }
}
