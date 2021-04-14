package com.housekeeping.order.controller;

import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2021/4/14 11:10
 */
@Api(tags={"【订单】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class SysOrderController {

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】获取全部订单")
    @GetMapping
    public R list(){
        return R.ok();
    }

}
