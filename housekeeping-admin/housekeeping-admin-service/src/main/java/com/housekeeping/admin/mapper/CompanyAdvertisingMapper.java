package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.CompanyAdvertising;
import com.housekeeping.common.utils.R;



public interface CompanyAdvertisingMapper extends BaseMapper<CompanyAdvertising> {

    R getByRan(Integer ran);
}
