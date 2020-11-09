package com.housekeeping.admin.controller;

import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.CommonConstants;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(value="公司controller",tags={"公司详情信息管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyDetails")
public class CompanyDetailsController {

   private final ICompanyDetailsService companyDetailsService;

    @ApiOperation("修改公司信息")
    @LogFlag(description = "修改公司信息")
    @PostMapping("/update")
    public R updateCompany(@RequestBody CompanyDetails companyDetails){
        boolean result = companyDetailsService.updateById(companyDetails);
        if(result){
            return R.ok("修改成功");
        }else {
            return R.failed("请求参数错误");
        }
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
        String logo_url = companyDetailsService.getLogoUrlByUserId(userId);
        File file = new File(CommonConstants.HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_DEV + userId + "/" + logo_url);
        InputStream in = new FileInputStream(file);
        byte[] body = null;
        body = new byte[in.available()];
        in.read(body);

        HttpHeaders headers = new HttpHeaders();// 设置响应头
        headers.add("Content-Disposition",
                "attachment;filename=" + logo_url);

        HttpStatus statusCode = HttpStatus.OK;// 设置响应吗
        return new ResponseEntity<byte[]>(body, headers, statusCode);
    }

    @ApiOperation("多圖片上傳接口,幾張都可以")
    @PostMapping("/uploadFiveImg")
    public R uploadFiveImg(@RequestParam("files") List<MultipartFile> files){
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储圖片
        String fileNames = companyDetailsService.uploadFiveImg(files, reviserId);
        //数据库存储圖片Url
        companyDetailsService.updateFiveImgUrlByUserId(fileNames, reviserId);
        return R.ok("圖片上傳成功");
    }

}
