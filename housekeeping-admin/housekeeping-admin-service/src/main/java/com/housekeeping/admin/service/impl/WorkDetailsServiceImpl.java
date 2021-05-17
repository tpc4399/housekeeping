package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.WorkDetails;
import com.housekeeping.admin.mapper.WorkDetailsMapper;
import com.housekeeping.admin.service.IWorkDetailsService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/4/28 16:11
 */
@Service("workDetailsService")
public class WorkDetailsServiceImpl
        extends ServiceImpl<WorkDetailsMapper, WorkDetails>
        implements IWorkDetailsService {
    @Override
    public void add(WorkDetails wd) {
        baseMapper.add(wd);
    }
}
