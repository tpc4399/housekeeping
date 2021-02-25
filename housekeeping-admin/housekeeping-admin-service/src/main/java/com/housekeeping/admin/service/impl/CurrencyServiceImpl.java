package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.service.ICurrencyService;
import com.housekeeping.common.utils.HttpUtils;
import com.housekeeping.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/1/12 15:59
 */
@Slf4j
@Service("currencyService")
public class CurrencyServiceImpl implements ICurrencyService {

    @Value("${currency.exchangeRate.host}")
    private String host;
    @Value("${currency.exchangeRate.path}")
    private String path;
    @Value("${currency.exchangeRate.path1}")
    private String path1;
    @Value("${currency.exchangeRate.method}")
    private String method;
    @Value("${currency.exchangeRate.appcode}")
    private String appcode;

    @Override
    public R exchangeRate(String fromCode, String toCode, BigDecimal money) {
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
            return R.failed("外汇接口出了點問題，請聯係系統人員");
        }
    }

    @Override
    public BigDecimal exchangeRateToBigDecimal(String fromCode, String toCode, BigDecimal money) {
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
            log.info("阿里云汇率换算接口被调用");
            JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            JSONObject showApiResBody = jsonObject.getJSONObject("showapi_res_body");
            String toMoney = showApiResBody.getString("money");
            return new BigDecimal(toMoney);
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal("-1");
        }
    }

    @Override
    public Map<String, Float> realTimeExchangeRate() {
        Map<String, Float> res = new HashMap<>();
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
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
            HttpResponse response = HttpUtils.doGet(host, path1, method, headers, querys);
            log.info("阿里云汇率接口调用");
            JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            JSONObject showApiResBody = jsonObject.getJSONObject("showapi_res_body");
            JSONArray list = showApiResBody.getJSONArray("list");
            list.forEach(x -> {
                String code = ((JSONObject)x).getString("code");
                String zs = ((JSONObject)x).getString("zhesuan");
                Float zsFloat = new Float(zs);
                res.put(code, zsFloat);
            });
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BigDecimal exchangeRateToBigDecimalAfterOptimization(String fromCode, String toCode, BigDecimal money) {

        return null;
    }

    @Test
    public void test(){
    }

}
