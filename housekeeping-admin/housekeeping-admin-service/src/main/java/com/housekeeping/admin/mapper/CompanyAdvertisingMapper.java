package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.CompanyAdvertising;
import com.housekeeping.admin.vo.CompanyAdvertingVo;
import com.housekeeping.common.utils.R;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CompanyAdvertisingMapper extends BaseMapper<CompanyAdvertising> {

    List<CompanyAdvertising> getByRan(@Param("typeId")Integer typeId,@Param("ran")Integer ran);

    R getAllProAd();

    List<CompanyAdvertingVo> getAllAdderByAdmin(@Param("id") Integer id, @Param("title") String title);
}
