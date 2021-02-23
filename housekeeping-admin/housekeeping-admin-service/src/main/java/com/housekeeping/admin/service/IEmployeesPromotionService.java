package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.admin.entity.EmployeesPromotion;
import com.housekeeping.common.utils.R;


public interface IEmployeesPromotionService extends IService<EmployeesPromotion> {



    R promotionDay(Integer empId);

    R promotionTenDay(Integer empId);

    R getEmpInfoByCompanyId(Integer empId,String empName);

    R getEmpByRan(Integer random);

    R getAllProEmp();
}
