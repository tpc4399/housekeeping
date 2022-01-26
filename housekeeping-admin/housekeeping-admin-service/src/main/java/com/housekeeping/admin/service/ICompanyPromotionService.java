package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.common.utils.R;


public interface ICompanyPromotionService extends IService<CompanyPromotion> {


    R getInfoById(Integer companyId);

    R promotionDay(Integer companyId);

    R promotionTenDay(Integer companyId);

    R getCompanyByRan(Integer random);

    R getAllProCompany();

    R promotionByAdmin(Integer companyId, Integer days);

    R getInfoByAdmin(Integer id, String name, Boolean status);

    Boolean getStatus(Integer companyId);
}
