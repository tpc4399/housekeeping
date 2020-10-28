package com.housekeeping.gateway.client;

import com.housekeeping.admin.api.AdminApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("admin-service")
public interface AdminClient extends AdminApi {
}
