package com.housekeeping.admin.controller;

import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/17 21:22
 */
@Api(value="订单管理controller",tags={"【订单】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysOrder")
public class SysOrderController {

    @PostMapping("/roturl")
    @ApiOperation("支付成功回调接口")
    public R paymentSuccessCallbackInterface(){
        return R.ok();
    }
}
