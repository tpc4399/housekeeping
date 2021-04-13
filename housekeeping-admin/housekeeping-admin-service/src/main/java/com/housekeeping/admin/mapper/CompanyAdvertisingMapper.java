package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.CompanyAdvertising;
import com.housekeeping.admin.vo.CompanyAdvertisingsVo;
import com.housekeeping.common.utils.R;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CompanyAdvertisingMapper extends BaseMapper<CompanyAdvertising> {

    R getByRan(Integer ran);

    R getAllProAd();

    List<CompanyAdvertisingsVo> getAllAdverByAdmin(@Param("id") Integer id, @Param("title") String title);
}
