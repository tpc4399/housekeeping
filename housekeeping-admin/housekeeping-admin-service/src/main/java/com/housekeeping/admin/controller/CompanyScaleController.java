package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.SetScaleDTO;
import com.housekeeping.admin.entity.CompanyScale;
import com.housekeeping.admin.service.ICompanyScaleService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @Date 2021/2/22 16:32
 */
@Api(tags={"【公司规模】字典管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyScale")
public class CompanyScaleController {

    private final ICompanyScaleService companyScaleService;

    @ApiOperation("【管理员】设置修改公司规模与价格")
    @PutMapping
    public R setScale(@RequestBody SetScaleDTO dto){
        return companyScaleService.setScale(dto);
    }


    @ApiOperation("【管理员】查询公司规模与价格")
    @GetMapping
    public R listScale(){
        return companyScaleService.listScale();
    }

    @ApiOperation("【管理员】【公司】【客户】【经理】【员工】查询公司详细规模")
    @GetMapping("/{id}")
    public R getDetails(@PathVariable Integer id){
        CompanyScale companyScale = companyScaleService.getById(id);
        String scale = companyScale.getScale();
        return R.ok(scale, "查询成功");
    }

}
