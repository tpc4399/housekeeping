package com.housekeeping.admin.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.mapper.CompanyDetailsMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
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
        String catalogue = CommonConstants.HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_PROV + reviserId;
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
        String catalogue = CommonConstants.HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_PROV + reviserId;
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

    @Override
    public void updateById(CompanyDetailsDTO companyDetailsDTO, Integer lastReviserId) {
        baseMapper.updateById(companyDetailsDTO, lastReviserId);
    }

    @Override
    public Integer getCompanyIdByUserId(Integer userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = baseMapper.selectOne(queryWrapper);
        return companyDetails.getId();
    }

    @Override
    public R pageOfCompanyByAdmin(IPage<CompanyDetails> page, CompanyDetailsPageDTO companyDetailsPageDTO) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getNumber())){
            queryWrapper.like("number", companyDetailsPageDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCompanyName())){
            queryWrapper.like("company_name", companyDetailsPageDTO.getCompanyName());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCompanySizeId())){
            queryWrapper.eq("company_size_id", companyDetailsPageDTO.getCompanySizeId());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getLegalPerson())){
            queryWrapper.eq("legal_person", companyDetailsPageDTO.getLegalPerson());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getIsValidate())){
            queryWrapper.eq("is_validate", companyDetailsPageDTO.getIsValidate());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getIndustrialNumber())){
            queryWrapper.eq("industrial_number", companyDetailsPageDTO.getIndustrialNumber());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress1())){
            queryWrapper.like("address1", companyDetailsPageDTO.getAddress1());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress2())){
            queryWrapper.like("address2", companyDetailsPageDTO.getAddress2());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress3())){
            queryWrapper.like("address3", companyDetailsPageDTO.getAddress3());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress4())){
            queryWrapper.like("address4", companyDetailsPageDTO.getAddress4());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getServiceHotline())){
            queryWrapper.like("service_hotline", companyDetailsPageDTO.getServiceHotline());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getEmail())){
            queryWrapper.like("email", companyDetailsPageDTO.getEmail());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getWebPages())){
            queryWrapper.like("web_pages", companyDetailsPageDTO.getWebPages());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAccountLine())){
            queryWrapper.like("account_line", companyDetailsPageDTO.getAccountLine());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getConnectionFacebook())){
            queryWrapper.like("connection_facebook", companyDetailsPageDTO.getConnectionFacebook());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getConnectionInstagram())){
            queryWrapper.like("connection_instagram", companyDetailsPageDTO.getConnectionInstagram());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getDescribes())){
            queryWrapper.like("describes", companyDetailsPageDTO.getDescribes());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getMethodPayment())){
            queryWrapper.like("method_payment", companyDetailsPageDTO.getMethodPayment());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCreateTimeStart())){
            queryWrapper.ge("create_time", companyDetailsPageDTO.getCreateTimeStart());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCreateTimeEnd())){
            queryWrapper.le("create_time", companyDetailsPageDTO.getCreateTimeEnd());
        }
        IPage<CompanyDetails> companyDetailsIPage = baseMapper.selectPage(page, queryWrapper);
        return R.ok(companyDetailsIPage, "查詢公司成功");
    }

    @Override
    public void authSuccess(Integer companyId) {
        baseMapper.authSuccess(companyId);
    }
}
