package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICompanyAdvertisingService;
import com.housekeeping.admin.vo.AdvertisingRenewalVo;
import com.housekeeping.admin.vo.AdvertisingVo;
import com.housekeeping.admin.vo.CompanyAdvertisingVo;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Api(tags={"【廣告推廣】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyAdvertising")
public class CompanyAdvertisingController {

    private final ICompanyAdvertisingService iCompanyAdvertisingService;

    @PostMapping
    @ApiOperation("【公司】廣告推廣(1/10天)")
    public R add(@RequestBody CompanyAdvertisingVo companyAdvertising){
        return iCompanyAdvertisingService.add(companyAdvertising);
    }

    @PutMapping
    @ApiOperation("【公司】修改廣告推廣")
    public R update(@RequestBody AdvertisingVo companyAdvertising){
        return iCompanyAdvertisingService.cusUpdate(companyAdvertising);
    }

    @PutMapping("/renewal")
    @ApiOperation("【公司】續費廣告推廣")
    public R renewal(@RequestBody AdvertisingRenewalVo companyAdvertising){
        return iCompanyAdvertisingService.renewal(companyAdvertising);
    }

    @GetMapping("/getByCompanyId")
    @ApiOperation("【公司】查詢公司的廣告")
    public R getByCompanyId(Integer companyId,Integer id,String name){
        return iCompanyAdvertisingService.getByCompanyId(companyId, id, name);
    }

    @GetMapping("getByRan")
    @ApiOperation("【公司】根據數量隨機獲取推薦廣告")
    public R getByRan(Integer ran){
        return iCompanyAdvertisingService.getByRan(ran);
    }

    @ApiOperation("【公司】廣告上傳圖片")
    @PostMapping("/uploadPhoto")
    public R uploadPhoto(@RequestParam("file") MultipartFile file)  {
        return iCompanyAdvertisingService.uploadPhoto(file);
    }


}
