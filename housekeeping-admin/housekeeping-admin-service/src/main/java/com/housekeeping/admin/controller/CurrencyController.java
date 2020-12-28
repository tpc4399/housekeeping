package com.housekeeping.admin.controller;

import com.housekeeping.common.utils.HttpUtils;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
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

    @Value("${currency.exchangeRate.host}")
    private String host;
    @Value("${currency.exchangeRate.path}")
    private String path;
    @Value("${currency.exchangeRate.method}")
    private String method;
    @Value("${currency.exchangeRate.appcode}")
    private String appcode;

    @ApiOperation("实时汇率换算")
    @GetMapping("/exchangeRate")
    public R exchangeRate(@RequestParam("fromCode") String fromCode,
                            @RequestParam("toCode") String toCode,
                            @RequestParam("money") BigDecimal money){

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("fromCode", fromCode);
        querys.put("money", money.toString());
        querys.put("toCode", toCode);


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            return R.ok(jsonObject, "轉換成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("出了點問題，請聯係系統人員");
        }

    }

}
