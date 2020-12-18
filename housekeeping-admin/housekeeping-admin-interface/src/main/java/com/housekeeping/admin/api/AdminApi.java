package com.housekeeping.admin.api;

import com.housekeeping.admin.entity.Log;
import com.housekeeping.admin.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface AdminApi {

    @GetMapping("/user/byEmail")
    public User getUserByEmail(@RequestParam String email);

    @GetMapping("/user/byPhone")
    public User getUserByPhone(@RequestParam String phone);

    @GetMapping("/log/addOne")
    public void addOne(Log log);

}
