package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICurrencyService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/18 16:53
 */
@Api(tags={"【ZZ】单元测试接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final ICurrencyService currencyService;

    @GetMapping
    @ApiOperation("单元测试调试接口")
    public R test(){
        currencyService.exchangeRateToBigDecimal("TWD", "CNY", BigDecimal.valueOf(100));
        return R.ok();
    }

}
