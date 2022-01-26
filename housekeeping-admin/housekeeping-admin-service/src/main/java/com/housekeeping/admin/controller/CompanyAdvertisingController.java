package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.AdvertisingRenewalAdminVo;
import com.housekeeping.admin.dto.CompanyAdvertisingAdminVo;
import com.housekeeping.admin.service.ICompanyAdvertisingService;
import com.housekeeping.admin.vo.AdvertisingRenewalVo;
import com.housekeeping.admin.vo.AdvertisingVo;
import com.housekeeping.admin.vo.CompanyAdvertisingVo;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Api(tags={"【广告推广】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyAdvertising")
public class CompanyAdvertisingController {

    private final ICompanyAdvertisingService iCompanyAdvertisingService;

    @PostMapping
    @ApiOperation("廣告推廣(1/10天)")
    public R add(@RequestBody CompanyAdvertisingVo companyAdvertising){
        return iCompanyAdvertisingService.add(companyAdvertising);
    }

    @PutMapping("/renewal")
    @ApiOperation("【公司】續費廣告推廣")
    public R renewal(@RequestBody AdvertisingRenewalVo companyAdvertising){
        return iCompanyAdvertisingService.renewal(companyAdvertising);
    }

    @PostMapping("/addByAdmin")
    @ApiOperation("【管理员】廣告推廣")
    public R addByAdmin(@RequestBody CompanyAdvertisingAdminVo companyAdvertising){
        return iCompanyAdvertisingService.addByAdmin(companyAdvertising);
    }

    @PutMapping
    @ApiOperation("【公司】【管理员】修改廣告推廣")
    public R update(@RequestBody AdvertisingVo companyAdvertising){
        return iCompanyAdvertisingService.cusUpdate(companyAdvertising);
    }

    @PutMapping("/renewalByAdmin")
    @ApiOperation("【管理员】續費廣告推廣")
    public R renewalByAdmin(@RequestBody AdvertisingRenewalAdminVo companyAdvertising){
        return iCompanyAdvertisingService.renewalByAdmin(companyAdvertising);
    }

    @GetMapping("/getByCompanyId")
    @ApiOperation("【公司】查詢公司的廣告")
    public R getByCompanyId(Integer companyId,Integer typeId,Integer id,String name){
        return iCompanyAdvertisingService.getByCompanyId(companyId,typeId, id, name);
    }

    @GetMapping("/getByAdmin")
    @ApiOperation("【管理员】查詢所有廣告（推广）")
    public R getByAdmin(Integer id,String title,Boolean status){
        return iCompanyAdvertisingService.getByAdmin(id, title,status);
    }

    @GetMapping("/getByUserId")
    @ApiOperation("【管理员】查詢公司推广中的廣告")
    public R getByUserId(Integer userId,Integer id,String name){
        return iCompanyAdvertisingService.getByUserId(userId, id, name);
    }

    @GetMapping("getByRan")
    @ApiOperation("根據數量隨機獲取推薦廣告(0大广告 1小广告)")
    public R getByRan(Integer typeId,Integer ran){
        return iCompanyAdvertisingService.getByRan(typeId,ran);
    }

    @ApiOperation("【公司】【管理员】廣告上傳圖片")
    @PostMapping("/uploadPhoto")
    public R uploadPhoto(@RequestParam("file") MultipartFile file)  {
        return iCompanyAdvertisingService.uploadPhoto(file);
    }

    @DeleteMapping("/remove")
    @ApiOperation("【公司】【管理员】删除广告")
    public R remove(Integer id){
        return R.ok(iCompanyAdvertisingService.removeById(id));
    }

    @GetMapping("/getAllAdverByAdmin")
    @ApiOperation("【管理员】广告列表查詢所有廣告")
    public R getAllAdverByAdmin(Page page, Integer id, String name, Boolean status){
        return iCompanyAdvertisingService.getAllAdverByAdmin(page,id, name,status);
    }
}
