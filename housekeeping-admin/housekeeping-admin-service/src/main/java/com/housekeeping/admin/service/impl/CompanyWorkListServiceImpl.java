package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CompanyWorkListMapper;
import com.housekeeping.admin.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author su
 * @create 2020/11/18 16:10
 */
@Service("companyWorkListService")
public class CompanyWorkListServiceImpl extends ServiceImpl<CompanyWorkListMapper, CompanyWorkList> implements ICompanyWorkListService {

    @Resource
    private IGroupEmployeesService groupEmployeesService;
    @Resource
    private ISysOrderPlanService sysOrderPlanService;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesWorksheetPlanService employeesWorksheetPlanService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private ICustomerDemandPlanService customerDemandPlanService;



}
