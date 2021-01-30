package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.admin.mapper.EmployeesContractMapper;
import com.housekeeping.admin.service.IEmployeesContractService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/1/30 17:22
 */
@Service("employeesContractService")
public class EmployeesContractServiceImpl
        extends ServiceImpl<EmployeesContractMapper, EmployeesContract>
        implements IEmployeesContractService {
}
