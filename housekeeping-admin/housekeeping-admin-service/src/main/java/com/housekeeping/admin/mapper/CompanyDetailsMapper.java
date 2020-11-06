package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.CompanyDetails;
import org.apache.ibatis.annotations.Param;

public interface CompanyDetailsMapper extends BaseMapper<CompanyDetails> {
    void updateLogoUrlById(@Param("logoUrl") String logoUrl,
                           @Param("reviserId") Integer reviserId);
    String getLogoUrlByUserId(Integer userId);
}
