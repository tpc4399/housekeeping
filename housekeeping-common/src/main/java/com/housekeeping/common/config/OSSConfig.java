package com.housekeeping.common.config;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @Author su
 * @Date 2020/12/10 9:25
 */
@Configurable
public class OSSConfig {

    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Bean("ossClient")
    public OSSClient ossClient(){
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

}
