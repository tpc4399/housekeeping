package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.dto.CompanyDetailsPromotionDTO;
import com.housekeeping.admin.dto.CompanyPromotionDTO;
import com.housekeeping.admin.entity.CompanyPromotion;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CompanyPromotionMapper extends BaseMapper<CompanyPromotion> {
    CompanyPromotionDTO getInfoById(Integer companyId);

    List<Integer> getCompanyByRan(Integer random);

    List<Integer> getAllProCompIds();

    List<CompanyDetailsPromotionDTO> getInfoByAdmin(@Param("id") Integer id,@Param("name") String name);
}
