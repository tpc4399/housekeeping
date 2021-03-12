package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICompanyPromotionService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(tags={"【公司推廣】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyPromotion")
public class CompanyPromotionController {

    private final ICompanyPromotionService companyPromotionService;

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】查詢當前狀態")
    @GetMapping("/getInfoByCompanyId")
    public R getInfoById(Integer companyId){
        return companyPromotionService.getInfoById(companyId);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】推廣一天")
    @GetMapping("/promotionDay")
    public R promotionDay(Integer companyId){
        return companyPromotionService.promotionDay(companyId);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】推廣十天")
    @GetMapping("/promotionTenDay")
    public R promotionTenDay(Integer companyId){
        return companyPromotionService.promotionTenDay(companyId);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】從推廣公司中隨機取")
    @GetMapping("/getCompanyByRan")
    public R getCompanyByRan(Integer random){
        return companyPromotionService.getCompanyByRan(random);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】查询所有推广公司")
    @GetMapping("/getAllProCompany")
    public R getAllProCompany(){
        return companyPromotionService.getAllProCompany();
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】查询所有公司（推广状态）")
    @GetMapping("/getInfoByAdmin")
    public R getInfoByAdmin(Integer id,String name,Boolean status){
        return companyPromotionService.getInfoByAdmin(id,name,status);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】推廣公司")
    @GetMapping("/promotionByAdmin")
    public R promotionByAdmin(Integer companyId,Integer days){
        return companyPromotionService.promotionByAdmin(companyId,days);
    }
}
