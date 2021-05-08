package com.housekeeping.admin.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.dto.CompanyDetailsUpdateDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;

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

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】【管理员】修改公司信息")
    @LogFlag(description = "修改公司信息")
    @PostMapping("/update")
    public R updateCompany(@RequestBody CompanyDetailsDTO companyDetailsDTO){
        companyDetailsService.updateById(companyDetailsDTO, TokenUtils.getCurrentUserId());
        return R.ok("修改成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】【管理员】修改公司信息，新接口")
    @LogFlag(description = "修改公司信息")
    @PostMapping("/update2")
    public R updateCompany2(@RequestBody CompanyDetailsUpdateDTO dto){
        return companyDetailsService.updateById(dto, TokenUtils.getCurrentUserId());
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】给公司上传logo")
    @LogFlag(description = "公司上传logo")
    @PostMapping("/uploadLogoByAdmin")
    public R uploadLogo(@RequestParam("file") MultipartFile file,
                        @RequestParam("userId")Integer userId) throws IOException {
        //服务器存储logo
        String fileName = companyDetailsService.uploadLogo(file, userId);
        //数据库存储logoUrl
        companyDetailsService.updateLogUrlByUserId(fileName, userId);
        return R.ok("logo保存成功");
    }

    @Access({RolesEnum.USER_COMPANY})
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

    @Access({RolesEnum.USER_COMPANY})
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

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】多圖片上傳接口,幾張都可以，盡量用postman測試這個接口，swagger會出問題(圖片數據為空，程序不會報錯)")
    @PostMapping(value = "/uploadFiveImgByAdmin", headers = "content-type=multipart/form-data")
    public R uploadFiveImg(@RequestParam("file") MultipartFile[] file,
                           @RequestParam("userId")Integer userId){
        //服务器存储圖片
        String fileNames = companyDetailsService.uploadFiveImg(file, userId);
        //数据库存储圖片Url
        companyDetailsService.updateFiveImgUrlByUserId(fileNames, userId);
        return R.ok("圖片上傳成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY})
    @ApiOperation("【公司、管理员】获取公司详情信息")
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

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】分页查询所有公司")
    @GetMapping("/pageOfCompany")
    public R pageOfCompany(Page page, CompanyDetailsPageDTO companyDetailsPageDTO){
        return companyDetailsService.pageOfCompanyByAdmin(page, companyDetailsPageDTO);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】新公司领取5代币")
    @GetMapping("/getFiveTokens")
    public R getFiveTokens(){
        return companyDetailsService.getFiveTokens();
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】購買一百代幣")
    @GetMapping("/buyHundredTokens")
    public R buyHundredTokens(){
        return companyDetailsService.buyHundredTokens();
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】購買一千代幣")
    @GetMapping("/buyThousandTokens")
    public R buyThousandTokens(){
        return companyDetailsService.buyThousandTokens();
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】1查询月或者年应缴纳费用(0月费用 1年费用)")
    @GetMapping("/getPay")
    public R getPay(@RequestParam("type") Integer type){
        return companyDetailsService.getPay(type);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】2缴费完成(0月费用 1年费用)")
    @GetMapping("/pay")
    public R pay(@RequestParam("type") Integer type){
        return companyDetailsService.pay(type);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】校验公司是否需要按照规模缴费（true需要续费 false不需要续费）")
    @GetMapping("checkCompPay")
    public R checkCompPay(@RequestParam("companyId")Integer companyId){
        return R.ok(companyDetailsService.checkCompPay(companyId));
    }

    @ApiOperation("首页查看所有公司")
    @GetMapping("/getAllCompany")
    public R getAllCompany(Page page,CompanyDetails companyDetails){
        return R.ok(companyDetailsService.page(page, Wrappers.query(companyDetails)));
    }

}
