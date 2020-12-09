package com.housekeeping.admin.controller;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @ApiOperation("加載logo接口")
    @GetMapping(value = "/getLogo",produces = MediaType.IMAGE_JPEG_VALUE)
    public void getFile(@RequestParam("userId") Integer userId, HttpServletResponse response) throws IOException {
        String logoName = companyDetailsService.getLogoUrlByUserId(userId);
        File file = new File(CommonConstants.HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_PROV + userId + "/" + logoName);
        BufferedImage image = ImageIO.read(file);
        OutputStream os = response.getOutputStream();
        ImageIO.write(image, "JPG", os);
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

    @ApiOperation("獲取公司照片數量")
    @GetMapping("/getPhotosNumber")
    public R getPhotosNumber(@RequestParam("userId") Integer userId){
        Integer number = 0;
        String photoNamePrefix = companyDetailsService.getPhotosByUserId(userId);
        File file = new File(CommonConstants.HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_PROV + userId);
        File[] parentFiles = file.listFiles();
        for (File parentFile : parentFiles) {
            String fileName = parentFile.getName();
            if (fileName.startsWith(photoNamePrefix)){
                number ++;
            }
        }
        return R.ok(number, "圖片張數獲取成功");
    }

    @ApiOperation("加載公司照片的接口")
    @GetMapping("/getPhotos")
    public void getFiveImg(@RequestParam("userId") Integer userId,
                           @RequestParam("index") Integer index,
                           HttpServletResponse response) throws IOException {
        String photoNamePrefix = companyDetailsService.getPhotosByUserId(userId);
        String fileNamePrefix = photoNamePrefix + "["+ index +"]";
        File file = new File(CommonConstants.HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_PROV + userId);
        File[] parentFiles = file.listFiles();
        Arrays.stream(parentFiles).forEach(parentFile -> {
            String fileName = parentFile.getName();
            if (fileName.startsWith(fileNamePrefix)){
                BufferedImage image = null;
                OutputStream os = null;
                try {
                    image = ImageIO.read(parentFile);
                    os = response.getOutputStream();
                    ImageIO.write(image, "JPG", os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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

}
