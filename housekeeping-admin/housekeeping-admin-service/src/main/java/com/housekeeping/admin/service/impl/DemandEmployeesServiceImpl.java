package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesInstanceDTO;
import com.housekeeping.admin.entity.DemandEmployees;
import com.housekeeping.admin.entity.DemandOrderDetails;
import com.housekeeping.admin.mapper.DemandEmployeesMapper;
import com.housekeeping.admin.mapper.DemandOrderDetailsMapper;
import com.housekeeping.admin.service.IAsyncService;
import com.housekeeping.admin.service.IDemandEmployeesService;
import com.housekeeping.admin.service.IDemandOrderDetailsService;
import com.housekeeping.common.utils.RedisUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
