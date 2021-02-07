package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.AdminAdd1DTO;
import com.housekeeping.admin.dto.AdminAdd2DTO;
import com.housekeeping.admin.dto.PageOfUserDTO;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @Date 2021/2/7 15:47
 */
@Api(tags={"【用户管理】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysUser")
public class SysUserController {

    private final IUserService userService;

    @GetMapping("/page")
    @ApiOperation("【管理员】分页查询用户")
    public R page(Page page, PageOfUserDTO dto){
        return userService.page(page, dto);
    }

    @PostMapping("/addAccount1")
    @ApiOperation("【管理员】添加管理员、公司、家庭账户接口")
    public R add1(@RequestBody AdminAdd1DTO dto){
        return userService.add1(dto);
    }

    @PostMapping("/addAccount2")
    @ApiOperation("【管理员】添加经理、保洁员接口")
    public R add2(@RequestBody AdminAdd2DTO dto){
        return userService.add2(dto);
    }

}
