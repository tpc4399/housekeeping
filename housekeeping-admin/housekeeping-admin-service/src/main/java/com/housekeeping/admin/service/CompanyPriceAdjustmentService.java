package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyPriceAdjustment;
import com.housekeeping.admin.entity.EmployeesPriceAdjustment;
import com.housekeeping.common.utils.R;


public interface CompanyPriceAdjustmentService extends IService<CompanyPriceAdjustment> {
    R add(CompanyPriceAdjustment companyPriceAdjustment);

    R cusUpdate(CompanyPriceAdjustment companyPriceAdjustment);

    R getAll(Integer id, Integer companyId);

    R copyByEmp(Integer id, Integer empId);
}
