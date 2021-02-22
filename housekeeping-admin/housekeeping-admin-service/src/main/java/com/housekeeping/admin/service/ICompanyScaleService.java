package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SetScaleDTO;
import com.housekeeping.admin.entity.CompanyScale;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/2/22 16:23
 */
public interface ICompanyScaleService extends IService<CompanyScale> {

    R setScale(SetScaleDTO dto);
    R listScale();

}
