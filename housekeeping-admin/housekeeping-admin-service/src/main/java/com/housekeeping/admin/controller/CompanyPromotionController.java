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
    @GetMapping("/getInfoById")
    public R getInfoById(Integer id){
        return iCompanyPromotionService.getInfoById(id);
    }


}
