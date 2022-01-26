package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.entity.SysContendPrice;
import com.housekeeping.admin.service.SysContendPriceService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;


/**
 * @Author su
 * @Date 2020/12/11 16:09
 */
@Api(tags={"【工作类型價格】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysContendPrice")
public class SysContendPriceController {

    private final SysContendPriceService sysContendPriceService;

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping
    @ApiOperation("【管理员】工作类型增加价格")
    public R add(@RequestBody SysContendPrice sysContendPrice){
        QueryWrapper<SysContendPrice> qw = new QueryWrapper<>();
        qw.eq("contend_id",sysContendPrice.getContendId());
        int count = sysContendPriceService.count(qw);
        if(count>=1){
            return R.failed("已存在相同工作内容價格");
        }
        return R.ok(sysContendPriceService.save(sysContendPrice));
    }


    @Access({RolesEnum.SYSTEM_ADMIN})
    @PutMapping("/update")
    @ApiOperation("【管理员】修改工作类型")
    public R update(@RequestBody SysContendPrice syscontendPrice){
        return R.ok(sysContendPriceService.updateById(syscontendPrice));
    }

    @ApiOperation("獲取所有工作类型價格")
    @GetMapping("/getAll")
    public R getAll(){
        List<SysContendPrice> list = sysContendPriceService.list();
        return R.ok(list);
    }

    @ApiOperation("計算價格與時長")
    @GetMapping("/getPrice")
    public R getPrice(@RequestParam Integer contendId,
                      @RequestParam Integer flag){
        HashMap<String, Object> map = new HashMap<>();
        QueryWrapper<SysContendPrice> qw = new QueryWrapper<>();
        qw.eq("contend_id",contendId);
        SysContendPrice one = sysContendPriceService.getOne(qw);
        if(one==null){
            return R.ok(null);
        }
        if(one.getFlat()==null||one.getHour()==null||one.getCompanyPrice()==null||one.getPersonalPrice()==null){
            return R.ok(false);
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(flag).divide(BigDecimal.valueOf(one.getFlat()),0, RoundingMode.HALF_UP);
        BigDecimal multiply = bigDecimal.multiply(BigDecimal.valueOf(one.getHour()));
        map.put("hours",multiply);
        BigDecimal multiply1 = multiply.multiply(BigDecimal.valueOf(one.getCompanyPrice()));
        BigDecimal multiply2 = multiply.multiply(BigDecimal.valueOf(one.getPersonalPrice()));
        map.put("companyPrice",multiply1);
        map.put("personalPrice",multiply2);
        return R.ok(map);
    }
}
