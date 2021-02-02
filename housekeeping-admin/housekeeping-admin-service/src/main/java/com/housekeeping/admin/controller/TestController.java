package com.housekeeping.admin.controller;

import com.google.maps.errors.ApiException;
import com.housekeeping.admin.dto.AddressDetailsDTO;
import com.housekeeping.admin.service.IAddressCodingService;
import com.housekeeping.admin.service.ICurrencyService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/18 16:53
 */
@Api(tags={"【ZZ】单元测试接口"})
@RestController
@AllArgsConstructor
public class TestController {

    private final ICurrencyService currencyService;
    private final IAddressCodingService addressCodingService;

    @GetMapping("/test1")
    @ApiOperation("测试1")
    public R test1(){
        currencyService.exchangeRateToBigDecimal("TWD", "CNY", BigDecimal.valueOf(100));
        return R.ok();
    }

    @GetMapping("/test2")
    @ApiOperation("测试2")
    public R test2(){
        //把地址存為經緯度 湖北省武汉市洪山区茉莉公馆 高雄市苓雅区四维三路2号
        JSONObject jsonObject = (JSONObject) addressCodingService.addressCoding("高雄市议会台北联络处").getData();
        try {
            JSONObject result = (JSONObject) jsonObject.get("result");
            JSONObject location = (JSONObject) result.get("location");
            Double lng = (Double) location.get("lng");
            Double lat = (Double) location.get("lat");
        }catch (RuntimeException e){
            return R.failed("地址解析失敗");
        }
        return R.ok("解析成功");
    }

    @GetMapping("/test3")
    @ApiOperation("测试3")
    public R test3() throws InterruptedException, ApiException, IOException {
        AddressDetailsDTO addressDetailsDTO = addressCodingService.addressCodingGoogleMap("高雄市苓雅区四维三路2号");
        return R.ok("解析成功");
    }

}
