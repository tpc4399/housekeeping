package com.housekeeping.admin.feign;

import com.housekeeping.admin.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author su
 * @create 2020/10/27 1:28
 */
@FeignClient(value = "admin-service")
public interface UserFeign {
    @GetMapping("/api/admin/user/byMail")
    public User byEmail(@RequestParam("email") String email);
    @GetMapping("/api/admin/user/byPhone")
    public User byPhone(@RequestParam("phone") String phone);
}
