package com.housekeeping.gateway.client;

import com.housekeeping.auth.api.AuthApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author su
 * @create 2020/10/28 18:39
 */
@FeignClient("auth-service")
public interface AuthClient extends AuthApi {
}
