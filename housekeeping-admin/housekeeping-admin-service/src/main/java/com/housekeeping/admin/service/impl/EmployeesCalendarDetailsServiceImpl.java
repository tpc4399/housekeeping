package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.EmployeesCalendarDetails;
import com.housekeeping.admin.mapper.EmployeesCalendarDetailsMapper;
import com.housekeeping.admin.service.IEmployeesCalendarDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author su
 * @Date 2021/1/29 16:56
 */
@Transactional
@Service("employeesCalendarDetailsService")
public class EmployeesCalendarDetailsServiceImpl
        extends ServiceImpl<EmployeesCalendarDetailsMapper, EmployeesCalendarDetails>
        implements IEmployeesCalendarDetailsService {
}
