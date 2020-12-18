package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.GroupEmployees;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/19 14:57
 */
public interface GroupEmployeesMapper extends BaseMapper<GroupEmployees> {
    Integer count(Integer groupId);

    List<Integer> getIdsByGroupId(Integer groupId);
}
