package com.housekeeping.order.controller;

import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import com.housekeeping.order.dto.Action1DTO;
import com.housekeeping.order.dto.Action2DTO;
import com.housekeeping.order.service.IOrderIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @Date 2021/4/14 11:10
 */
@Api(tags={"【订单】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class SysOrderController {

    private final IOrderIdService orderIdService;

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】获取全部订单")
    @GetMapping
    public R list(){
        return R.ok();
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】预约钟点工操作，然后生成订单-->待付款")
    @PostMapping("/action1")
    public R action1(@RequestBody Action1DTO dto){
        return R.ok();
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】预约包工操作，然后生成订单-->待付款")
    @PostMapping("/action2")
    public R action2(@RequestBody Action2DTO dto){
        return R.ok();
    }

    @ApiOperation("预约包工操作，然后生成订单-->待付款")
    @GetMapping("/test")
    public R test(){
        System.out.println(orderIdService.generateId());
        return R.ok();
    }

}
