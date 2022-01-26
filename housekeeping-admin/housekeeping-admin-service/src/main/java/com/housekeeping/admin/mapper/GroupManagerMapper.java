package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.GroupManager;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/25 14:50
 */
public interface GroupManagerMapper extends BaseMapper<GroupManager> {
    Integer count(Integer groupId);

    List<Integer> getManIdsByGroupId(Integer groupId);
}
