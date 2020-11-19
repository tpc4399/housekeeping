package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.Group;
import com.housekeeping.admin.mapper.GroupMapper;
import com.housekeeping.admin.service.GroupService;
import org.springframework.stereotype.Service;

@Service("groupService")
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    @Override
    public R cusRemove(Integer id) {
        if(id != null){
            this.removeById(id);
            Boolean a = baseMapper.removeEmp(id);
            Boolean b = baseMapper.removeMan(id);
        }else {
            return R.failed("id爲空");
        }
        return R.ok("刪除分組成功");
    }

    @Override
    public IPage getGroup(Page page, Integer companyId, Integer id) {
        return baseMapper.getGroup(page,companyId,id);
    }

    @Override
    public R addMan(Integer groupId, Integer managerId) {
        return baseMapper.addMan(groupId, managerId);
    }
}
