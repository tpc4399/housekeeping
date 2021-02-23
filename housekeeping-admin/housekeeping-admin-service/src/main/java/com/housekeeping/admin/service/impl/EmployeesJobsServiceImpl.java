package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesJobsDTO;
import com.housekeeping.admin.dto.JobsDTO;
import com.housekeeping.admin.entity.EmployeesJobs;
import com.housekeeping.admin.mapper.EmployeesJobsMapper;
import com.housekeeping.admin.service.IEmployeesJobsService;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2021/1/11 10:23
 */
@Service("employeesJobsService")
public class EmployeesJobsServiceImpl
        extends ServiceImpl<EmployeesJobsMapper, EmployeesJobs>
        implements IEmployeesJobsService {

    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private IEmployeesJobsService employeesJobsService;

}
