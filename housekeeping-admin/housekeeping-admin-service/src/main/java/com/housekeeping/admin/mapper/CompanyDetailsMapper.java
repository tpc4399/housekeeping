package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
import com.housekeeping.admin.dto.CompanyDetailsUpdateDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import org.apache.ibatis.annotations.Param;

public interface CompanyDetailsMapper extends BaseMapper<CompanyDetails> {
    void updateLogoUrlById(@Param("logoUrl") String logoUrl,
                           @Param("reviserId") Integer reviserId);
    void updateFiveImgUrlByUserId(@Param("imgUrl") String imgUrl,
                                  @Param("reviserId") Integer reviserId);
    String getLogoUrlByUserId(Integer userId);
    String getPhotosByUserId(Integer userId);
    void updateById(@Param("companyDetailsDTO") CompanyDetailsDTO companyDetailsDTO,
                    @Param("lastReviserId") Integer lastReviserId);
    void updateById2(@Param("dto") CompanyDetailsUpdateDTO dto,
                    @Param("lastReviserId") Integer lastReviserId);
    void authSuccess(@Param("companyId") Integer companyId,
                     @Param("companyName") String companyName);

    void cusUpdateById(@Param("tokens") Integer tokens,@Param("id") Integer id);

    void buyTokens(@Param("tokens") Integer tokens,@Param("id") Integer id,@Param("buyTokens") int buyTokens);

    void promotion(@Param("companyId") Integer companyId,@Param("ctokens")  Integer ctokens,@Param("tokens") Integer tokens);

    Integer getScaleSwitch();
}
