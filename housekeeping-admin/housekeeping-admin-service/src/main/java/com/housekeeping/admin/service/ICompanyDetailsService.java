package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.common.utils.R;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ICompanyDetailsService extends IService<CompanyDetails> {
    String uploadLogo(MultipartFile file, Integer id) throws IOException;
    String uploadFiveImg(MultipartFile[] files, Integer id);
    R updateLogUrlByUserId(String logoUrl, Integer id);
    R updateFiveImgUrlByUserId(String imgUrl, Integer id);
    String getLogoUrlByUserId(Integer userId);
    String getPhotosByUserId(Integer userId);
    void updateById(CompanyDetailsDTO companyDetailsDTO, Integer lastReviserId);
    Integer getCompanyIdByUserId(Integer userId);
    R pageOfCompanyByAdmin(IPage<CompanyDetails> page, CompanyDetailsPageDTO companyDetailspageDTO);
    void authSuccess(Integer companyId, String companyName);
    R getFiveTokens();
    R buyHundredTokens();
    R buyThousandTokens();
    /* 判断我的公司下面有没有该保洁员 */
    Boolean thereIsACleaner(Integer employeesId);
}
