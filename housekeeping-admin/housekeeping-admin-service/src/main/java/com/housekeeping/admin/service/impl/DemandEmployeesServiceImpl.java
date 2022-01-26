package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.DemandEmployees;
import com.housekeeping.admin.mapper.DemandEmployeesMapper;
import com.housekeeping.admin.service.IDemandEmployeesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author su
 * @Date 2021/3/30 18:45
 */
@Service("demandEmployeesService")
public class DemandEmployeesServiceImpl extends ServiceImpl<DemandEmployeesMapper, DemandEmployees> implements IDemandEmployeesService {


    @Override
    public List<Integer> getAllUserId() {
        return baseMapper.getAllUserId();
    }
}
