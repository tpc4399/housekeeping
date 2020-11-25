package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.admin.mapper.GroupEmployeesMapper;
import com.housekeeping.admin.service.IGroupEmployeesService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @create 2020/11/19 14:56
 */
@Service("groupEmployeesService")
public class GroupEmployeesServiceImpl extends ServiceImpl<GroupEmployeesMapper, GroupEmployees> implements IGroupEmployeesService {
    @Override
    public R add(GroupEmployeesDTO groupEmployeesDTO) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        groupEmployeesDTO.getEmployeesId().forEach(x -> {
            GroupEmployees groupEmployees = new GroupEmployees();
            groupEmployees.setEmployeesId(x);
            groupEmployees.setGroupId(groupEmployeesDTO.getGroupId());
            //判断是否存在
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("group_id", groupEmployeesDTO.getGroupId());
            queryWrapper.eq("employees_id", x);
            List<GroupEmployees> res = baseMapper.selectList(queryWrapper);
            if ( res == null || res.size() == 0){
                baseMapper.insert(groupEmployees);
                count.getAndSet(count.get() + 1);
            }
        });
        Integer s = groupEmployeesDTO.getEmployeesId().size() - count.get();
        if (s == 0){
            return R.ok("分組成功添加"+count.get()+"個員工");
        }else {
            return R.ok("分組成功添加"+count.get()+"個員工,有" + s + "个员工是已经存在于该分组的");
        }
    }

    @Override
    public R delete(GroupEmployeesDTO groupEmployeesDTO) {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        groupEmployeesDTO.getEmployeesId().forEach(x -> {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("group_id", groupEmployeesDTO.getGroupId());
            queryWrapper.eq("employees_id", x);
            baseMapper.delete(queryWrapper);
            count.getAndSet(count.get() + 1);
        });
        return R.ok("分組成功刪除"+count.get()+"個員工");
    }

    @Override
    public R matchTheEmployees(Integer managerId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        // select employees_id from group_employees
        // where group_id in (select group_id from group_manager where manager_id = #{managerId})
        queryWrapper.inSql("group_id", "select group_id from group_manager where manager_id = " + managerId);
        return R.ok(baseMapper.selectList(queryWrapper), "经理"+ managerId +"管理的员工,获取成功");
    }
}
