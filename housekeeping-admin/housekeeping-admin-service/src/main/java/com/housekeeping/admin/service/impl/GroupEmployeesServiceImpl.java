package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.admin.mapper.GroupEmployeesMapper;
import com.housekeeping.admin.service.IGroupEmployeesService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2020/11/19 14:56
 */
@Service("groupEmployeesService")
public class GroupEmployeesServiceImpl extends ServiceImpl<GroupEmployeesMapper, GroupEmployees> implements IGroupEmployeesService {
}
