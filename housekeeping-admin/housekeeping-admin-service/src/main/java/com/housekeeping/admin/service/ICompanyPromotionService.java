package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.common.utils.R;


public interface ICompanyPromotionService extends IService<CompanyPromotion> {


    R getInfoById(Integer id);
}
