package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICompanyPromotionService;
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

    private final ICompanyPromotionService iCompanyPromotionService;

    @ApiOperation("【公司】查詢當前狀態")
    @GetMapping("/getInfoByCompanyId")
    public R getInfoById(Integer companyId){
        return iCompanyPromotionService.getInfoById(companyId);
    }

    @ApiOperation("【公司】推廣一天")
    @GetMapping("/promotionDay")
    public R promotionDay(Integer companyId){
        return iCompanyPromotionService.promotionDay(companyId);
    }

    @ApiOperation("【公司】推廣十天")
    @GetMapping("/promotionTenDay")
    public R promotionTenDay(Integer companyId){
        return iCompanyPromotionService.promotionTenDay(companyId);
    }

    @ApiOperation("【公司】從推廣公司中隨機取")
    @GetMapping("/getCompanyByRan")
    public R getCompanyByRan(Integer random){
        return iCompanyPromotionService.getCompanyByRan(random);
    }
}
