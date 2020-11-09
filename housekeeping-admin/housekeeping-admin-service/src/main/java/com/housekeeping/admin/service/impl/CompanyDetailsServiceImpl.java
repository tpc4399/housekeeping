package com.housekeeping.admin.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.mapper.CompanyDetailsMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.R;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


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
    public String uploadFiveImg(MultipartFile[] files, Integer reviserId) {
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_DEV + reviserId;
        File mkdir = new File(catalogue);
        if (!mkdir.exists()){
            mkdir.mkdirs();
        }
        AtomicReference<Integer> count = new AtomicReference<>(0);
        Arrays.stream(files).forEach(file -> {
            String fileType = file.getOriginalFilename().split("\\.")[1];
            String fileName = nowString + "[" + count.toString() + "]."+ fileType;
            String fileAbstractPath = catalogue + "/" + fileName;
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
            } finally {
                count.getAndSet(count.get() + 1);
            }
        });
        return nowString;
    }

    @Override
    public R updateLogUrlByUserId(String logoUrl, Integer id) {
        baseMapper.updateLogoUrlById(logoUrl, id);
        return R.ok("");
    }

    @Override
    public R updateFiveImgUrlByUserId(String imgUrl, Integer id) {
        baseMapper.updateFiveImgUrlByUserId(imgUrl, id);
        return null;
    }

    @Override
    public String getLogoUrlByUserId(Integer userId) {
        return baseMapper.getLogoUrlByUserId(userId);
    }

    @Override
    public String getPhotosByUserId(Integer userId) {
        return baseMapper.getPhotosByUserId(userId);
    }
}
