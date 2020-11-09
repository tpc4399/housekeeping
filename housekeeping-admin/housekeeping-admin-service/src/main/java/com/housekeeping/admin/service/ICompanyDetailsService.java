package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ICompanyDetailsService extends IService<CompanyDetails> {
    String uploadLogo(MultipartFile file, Integer id) throws IOException;
    String uploadFiveImg(MultipartFile[] files, Integer id);
    R updateLogUrlByUserId(String logoUrl, Integer id);
    R updateFiveImgUrlByUserId(String imgUrl, Integer id);
    String getLogoUrlByUserId(Integer userId);
}
