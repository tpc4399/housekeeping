package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.EmployeesPriceAdjustment;
import com.housekeeping.common.utils.R;


public interface IEmployeesPriceAdjustmentService extends IService<EmployeesPriceAdjustment> {
    R add(EmployeesPriceAdjustment employeesPriceAdjustment);

    R cusUpdate(EmployeesPriceAdjustment employeesPriceAdjustment);

    R getAll(Integer id, Integer empId);
}
