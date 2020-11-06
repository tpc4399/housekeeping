package com.housekeeping.admin.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.mapper.CompanyDetailsMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service("companyDetailsService")
public class CompanyDetailsServiceImpl extends ServiceImpl<CompanyDetailsMapper, CompanyDetails> implements ICompanyDetailsService {
    @Override
    public String uploadLogo(MultipartFile file, Integer reviserId) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_DEV + reviserId;
        File mkdir = new File(catalogue);
        if (!mkdir.exists()){
            mkdir.mkdirs();
        }

        String type = file.getOriginalFilename().split("\\.")[1];
        String fileName = nowString+"."+ type;
        String fileAbstractPath = catalogue + "/" + nowString+"."+ type;
        File resource = new File(fileAbstractPath);
        if (!resource.exists()){
            try {
                resource.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUtils.writeByteArrayToFile(resource, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    @Override
    public R updateLogUrlByUserId(String logoUrl, Integer id) {
        baseMapper.updateLogoUrlById(logoUrl, id);
        return R.ok("");
    }

    @Override
    public String getLogoUrlByUserId(Integer userId) {
        return baseMapper.getLogoUrlByUserId(userId);
    }
}
