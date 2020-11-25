package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.admin.mapper.GroupManagerMapper;
import com.housekeeping.admin.service.IGroupManagerService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2020/11/25 14:52
 */
@Service("groupManagerService")
public class GroupManagerServiceImpl extends ServiceImpl<GroupManagerMapper, GroupManager> implements IGroupManagerService {
}
