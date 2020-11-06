package com.housekeeping.admin.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/11/5 10:48
 */
@Api(value="用户详细信息controller",tags={"用户个人资料接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/userDetails")
public class UserDetailsController {
    
}
