package com.housekeeping.admin.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.housekeeping.admin.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("user")
public interface UserApi {

    @GetMapping("byEmail")
    public User getUserByEmail(@RequestParam String email);


    @GetMapping("byPhone")
    public User getUserByPhone(@RequestParam String phone);

}
