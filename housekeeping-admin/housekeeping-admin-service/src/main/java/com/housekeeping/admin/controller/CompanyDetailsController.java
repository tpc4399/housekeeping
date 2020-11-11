package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
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

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(value="公司controller",tags={"【公司】详情信息接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyDetails")
public class CompanyDetailsController {

   private final ICompanyDetailsService companyDetailsService;

    @ApiOperation("修改公司信息")
    @LogFlag(description = "修改公司信息")
    @PostMapping("/update")
    public R updateCompany(@RequestBody CompanyDetailsDTO companyDetailsDTO){
        companyDetailsService.updateById(companyDetailsDTO, TokenUtils.getCurrentUserId());
        return R.ok("修改成功");
    }

    @ApiOperation("公司上传logo")
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
    public ResponseEntity<byte[]> getFile(@RequestParam("userId") Integer userId) throws IOException {
        String logoName = companyDetailsService.getLogoUrlByUserId(userId);
        File file = new File(CommonConstants.HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_DEV + userId + "/" + logoName);
        InputStream in = new FileInputStream(file);
        byte[] body = null;
        body = new byte[in.available()];
        in.read(body);

        HttpHeaders headers = new HttpHeaders();// 设置响应头
        headers.add("Content-Disposition",
                "attachment;filename=" + logoName);

        HttpStatus statusCode = HttpStatus.OK;// 设置响应吗
        return new ResponseEntity<byte[]>(body, headers, statusCode);
    }

    @ApiOperation("多圖片上傳接口,幾張都可以，盡量用postman測試這個接口，swagger會出問題(圖片數據為空，程序不會報錯)")
    @PostMapping(value = "/uploadFiveImg", headers = "content-type=multipart/form-data")
    public R uploadFiveImg(@RequestParam("file") MultipartFile[] file){
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储圖片
        String fileNames = companyDetailsService.uploadFiveImg(file, reviserId);
        //数据库存储圖片Url
        companyDetailsService.updateFiveImgUrlByUserId(fileNames, reviserId);
        return R.ok("圖片上傳成功");
    }

    @ApiOperation("加載公司照片的接口")
    @GetMapping("/getPhotos")
    public R getFiveImg(@RequestParam("userId") Integer userId) throws IOException {
        String photoNamePrefix = companyDetailsService.getPhotosByUserId(userId);
        List<ResponseEntity<byte[]>> res = new ArrayList<>();
        File file = new File(CommonConstants.HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_DEV + userId);
        File[] parentFiles = file.listFiles();
        Arrays.stream(parentFiles).forEach(parentFile -> {
            String fileName = parentFile.getName();
            if (fileName.startsWith(photoNamePrefix)){
                InputStream in = null;
                try {
                    in = new FileInputStream(parentFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] body = null;
                try {
                    body = new byte[in.available()];
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.read(body);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                HttpHeaders headers = new HttpHeaders();// 设置响应头
                headers.add("Content-Disposition",
                        "attachment;filename=" + fileName);

                HttpStatus statusCode = HttpStatus.OK;// 设置响应吗
                res.add(new ResponseEntity<byte[]>(body, headers, statusCode));
            }
        });
        return R.ok(res);
    }

    @ApiOperation("获取公司详情信息")
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

}
