package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.service.IAddressCodingService;
import com.housekeeping.common.utils.HttpUtils;
import com.housekeeping.common.utils.R;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/1/12 16:31
 */
@Service("addressCodingService")
public class AddressCodingServiceImpl implements IAddressCodingService {

    @Value("${address.coding.host}")
    private String host;
    @Value("${address.coding.path1}")
    private String path1;
    @Value("${address.coding.path2}")
    private String path2;
    @Value("${address.coding.method_get}")
    private String methodGet;
    @Value("${address.coding.ak}")
    private String ak;

    @Override
    public R addressCoding(String address) {
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("address", address);
        params.put("output", "json");
        params.put("ak", ak);
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
            HttpResponse response = HttpUtils.doGet(host, path1, methodGet, headers, params);
            JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            JSONObject result = (JSONObject) jsonObject.get("result");
            JSONObject location = (JSONObject) result.get("location");
            Double lng = (Double) location.get("lng");
            Double lat = (Double) location.get("lat");
            return R.ok(jsonObject, "編碼成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("出了點問題，請聯係系統人員");
        }
    }

    @Override
    public Integer getInstanceByPointByWalking(String latitude1, String longitude1, String latitude2, String longitude2) {
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> params = new HashMap<String, String>();
        String origins = latitude1 + "," + longitude1;
        String destinations = latitude2 + "," + longitude2;
        params.put("origins", origins);
        params.put("destinations", destinations);
        params.put("output", "json");
        params.put("ak", ak);
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
            HttpResponse response = HttpUtils.doGet(host, path2, methodGet, headers, params);
            JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            JSONArray result =  jsonObject.getJSONArray("result");
            JSONObject distance = result.getJSONObject(0);
            JSONObject distance2 = distance.getJSONObject("distance");
            Integer meter = (Integer) distance2.get("value");
            return meter;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
