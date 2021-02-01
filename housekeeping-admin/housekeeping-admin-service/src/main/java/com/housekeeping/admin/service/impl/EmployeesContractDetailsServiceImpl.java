package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.EmployeesContractDetails;
import com.housekeeping.admin.mapper.EmployeesContractDetailsMapper;
import com.housekeeping.admin.service.IEmployeesContractDetailsService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/2/1 12:58
 */
@Service("employeesContractDetailsService")
public class EmployeesContractDetailsServiceImpl
        extends ServiceImpl<EmployeesContractDetailsMapper, EmployeesContractDetails>
        implements IEmployeesContractDetailsService {
}
