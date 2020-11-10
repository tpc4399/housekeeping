package com.housekeeping.auth.controller;

import com.housekeeping.auth.service.ISpecialLoginService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/11/10 16:27
 */
@Api(value="特殊登陆",tags={"特殊登陆接口"})
@RestController
@AllArgsConstructor
public class SpecialLoginController {

    private final ISpecialLoginService specialLoginService;

    @ApiOperation("【员工登入】链接方式")
    @GetMapping("/Employees/{key}")
    public R linkToLoginEmployees(@PathVariable String key){
        return specialLoginService.authEmployees(key);
    }

    @ApiOperation("【经理登入】链接方式")
    @GetMapping("/Manager/{key}")
    public R linkToLoginManager(@PathVariable String key){
        return R.ok();
    }
}
