package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.EmployeesWorksheetPlan;
import com.housekeeping.admin.mapper.EmployeesWorksheetPlanMapper;
import com.housekeeping.admin.service.IEmployeesWorksheetPlanService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2020/11/19 17:14
 */
@Service("employeesWorksheetPlan")
public class EmployeesWorksheetPlanServiceImpl
        extends ServiceImpl<EmployeesWorksheetPlanMapper, EmployeesWorksheetPlan>
        implements IEmployeesWorksheetPlanService {

    @Override
    public R getWorkSheetPlanByEmployeesId(Integer employeesId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("employees_id", employeesId);
        return R.ok(baseMapper.selectList(queryWrapper));
    }

}
