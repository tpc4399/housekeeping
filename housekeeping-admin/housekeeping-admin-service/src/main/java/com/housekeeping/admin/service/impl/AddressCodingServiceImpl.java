package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.service.IAddressCodingService;
import com.housekeeping.common.utils.HttpUtils;
import com.housekeeping.common.utils.R;
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
    @Value("${address.coding.path}")
    private String path;
    @Value("${address.coding.method}")
    private String method;
    @Value("${address.coding.ak}")
    private String ak;

    @Override
    public R addressCoding(String address) {
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("address", address);
        querys.put("output", "json");
        querys.put("ak", ak);
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
            JSONObject result = (JSONObject) jsonObject.get("result");
            JSONObject location = (JSONObject) result.get("location");
            Double lng = (Double) location.get("lng");
            Double lat = (Double) location.get("lat");
            System.out.println(lng);
            System.out.println(lat);

            return R.ok(jsonObject, "編碼成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("出了點問題，請聯係系統人員");
        }
    }
}
