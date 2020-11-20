package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.admin.mapper.GroupEmployeesMapper;
import com.housekeeping.admin.service.IGroupEmployeesService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2020/11/19 14:56
 */
@Service("groupEmployeesService")
public class GroupEmployeesServiceImpl extends ServiceImpl<GroupEmployeesMapper, GroupEmployees> implements IGroupEmployeesService {
    @Override
    public R matchTheEmployees(Integer managerId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        // select employees_id from group_employees
        // where group_id in (select group_id from group_manager where manager_id = #{managerId})
        queryWrapper.inSql("group_id", "select group_id from group_manager where manager_id = " + managerId);
        return R.ok(baseMapper.selectList(queryWrapper), "经理"+ managerId +"管理的员工,获取成功");
    }
}
