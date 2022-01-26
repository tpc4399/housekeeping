package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.EmployeesWorksheetPlan;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/19 17:14
 */
public interface IEmployeesWorksheetPlanService extends IService<EmployeesWorksheetPlan> {

    R getWorkSheetPlanByEmployeesId(Integer employeesId);

}
