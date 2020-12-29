package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyPromotionDTO;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.admin.mapper.CompanyPromotionMapper;
import com.housekeeping.admin.service.ICompanyPromotionService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

@Service("companyPromotionService")
public class CompanyPromotionServiceImpl extends ServiceImpl<CompanyPromotionMapper, CompanyPromotion> implements ICompanyPromotionService {

    @Override
    public R getInfoById(Integer id) {
        CompanyPromotionDTO companyPromotionDTO = baseMapper.getInfoById(id);
        return R.ok(companyPromotionDTO);
    }
}
