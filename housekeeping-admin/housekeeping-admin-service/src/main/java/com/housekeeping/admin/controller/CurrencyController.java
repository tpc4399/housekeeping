package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICurrencyService;
import com.housekeeping.common.utils.HttpUtils;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 货币处理接口
 * @Author su
 * @Date 2020/12/28 16:27
 */
@Api(tags={"【货币】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/currency")
public class CurrencyController {

    private final ICurrencyService currencyService;

    @ApiOperation("实时汇率换算")
    @GetMapping("/exchangeRate")
    public R exchangeRate(@RequestParam("fromCode") String fromCode,
                            @RequestParam("toCode") String toCode,
                            @RequestParam("money") BigDecimal money){
        return currencyService.exchangeRate(fromCode, toCode, money);
    }

    @ApiOperation("实时汇率查询")
    @GetMapping("/list")
    public R list(){
        Map<String, Float> map = currencyService.realTimeExchangeRate();
        return R.ok(map);
    }

}
