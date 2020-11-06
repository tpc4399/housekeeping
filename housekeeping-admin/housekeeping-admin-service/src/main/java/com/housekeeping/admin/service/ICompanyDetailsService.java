package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.common.utils.R;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ICompanyDetailsService extends IService<CompanyDetails> {
    String uploadLogo(MultipartFile file, Integer id) throws IOException;
    R updateLogUrlByUserId(String logoUrl, Integer id);
    String getLogoUrlByUserId(Integer userId);
}
