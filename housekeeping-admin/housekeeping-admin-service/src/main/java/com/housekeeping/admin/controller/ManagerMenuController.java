package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IManagerMenuService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2021/1/13 17:46
 */
@Api(tags = "【經理&菜單】中间表接口")
@AllArgsConstructor
@RestController
@RequestMapping("/managerMenu")
public class ManagerMenuController {

    private final IManagerMenuService managerMenuService;

    @ApiOperation("【公司】修改经理权限")
    @PutMapping
    public R updateManagerMenu(){
        return R.ok();
    }

    @ApiOperation("【公司】获取经理权限")
    @GetMapping
    public R getManagerMenu(Integer managerId){
        return R.ok();
    }

}
