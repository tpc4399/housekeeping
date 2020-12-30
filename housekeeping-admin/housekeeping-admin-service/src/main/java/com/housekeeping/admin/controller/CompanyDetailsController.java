package com.housekeeping.admin.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(value="公司controller",tags={"【公司详情】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyDetails")
public class CompanyDetailsController {

    private final ICompanyDetailsService companyDetailsService;

    @Resource
    private OSSClient ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @ApiOperation("【公司】修改公司信息")
    @LogFlag(description = "修改公司信息")
    @PostMapping("/update")
    public R updateCompany(@RequestBody CompanyDetailsDTO companyDetailsDTO){
        companyDetailsService.updateById(companyDetailsDTO, TokenUtils.getCurrentUserId());
        return R.ok("修改成功");
    }

    @ApiOperation("【公司】公司上传logo")
    @LogFlag(description = "公司上传logo")
    @PostMapping("/uploadLogo")
    public R uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储logo
        String fileName = companyDetailsService.uploadLogo(file, reviserId);
        //数据库存储logoUrl
        companyDetailsService.updateLogUrlByUserId(fileName, reviserId);
        return R.ok("logo保存成功");
    }

    @ApiOperation("【公司】多圖片上傳接口,幾張都可以，盡量用postman測試這個接口，swagger會出問題(圖片數據為空，程序不會報錯)")
    @PostMapping(value = "/uploadFiveImg", headers = "content-type=multipart/form-data")
    public R uploadFiveImg(@RequestParam("file") MultipartFile[] file){
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储圖片
        String fileNames = companyDetailsService.uploadFiveImg(file, reviserId);
        //数据库存储圖片Url
        companyDetailsService.updateFiveImgUrlByUserId(fileNames, reviserId);
        return R.ok("圖片上傳成功");
    }

    @ApiOperation("【公司】获取公司详情信息")
    @GetMapping("/details")
    public R getCompanyDetailsByUserId(@RequestParam("userId") Integer userId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(queryWrapper);
        if (CommonUtils.isNotEmpty(companyDetails)){
            return R.ok(companyDetails);
        } else {
            return R.failed("公司不存在");
        }
    }

    @ApiOperation("【管理员】分页查询所有公司")
    @GetMapping("/pageOfCompany")
    public R pageOfCompany(Page page, CompanyDetailsPageDTO companyDetailsPageDTO){
        return companyDetailsService.pageOfCompanyByAdmin(page, companyDetailsPageDTO);
    }

    @ApiOperation("【公司】新公司领取5代币")
    @GetMapping("/getFiveTokens")
    public R getFiveTokens(){
        return companyDetailsService.getFiveTokens();
    }

    @ApiOperation("【公司】購買一百代幣")
    @GetMapping("/buyHundredTokens")
    public R buyHundredTokens(){
        return companyDetailsService.buyHundredTokens();
    }

    @ApiOperation("【公司】購買一千代幣")
    @GetMapping("/buyThousandTokens")
    public R buyThousandTokens(){
        return companyDetailsService.buyThousandTokens();
    }

}
