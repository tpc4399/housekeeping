package com.housekeeping.gateway.client;

import com.housekeeping.admin.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("admin-service")
public interface UserClient extends UserApi {
}
