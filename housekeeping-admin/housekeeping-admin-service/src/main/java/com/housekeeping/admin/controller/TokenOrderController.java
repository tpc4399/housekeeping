package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.PayToken;
import com.housekeeping.admin.service.TokenOrderService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags={"代币支付接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/tokenOrder")
public class TokenOrderController {

    private final TokenOrderService tokenOrderService;

    @ApiOperation("第一步购买代币")
    @PostMapping("/payToken")
    public R payToken(@RequestBody PayToken dto){
        return tokenOrderService.payToken(dto);
    }

    @ApiOperation("确认订单界面——支付操作需要调用,订单状态———— 未支付->处理中")
    @PutMapping(value = "/pay", headers = "content-type=multipart/form-data")
    public R pay(@RequestParam("number") Long number, @RequestParam("payType") String payType){
        return tokenOrderService.pay(number, payType);
    }

    @ApiOperation("订单作废")
    @GetMapping("/payment")
    public R payment1(String number){
        //调用“订单作废”服务
        return tokenOrderService.inputSql(number);
    }

    @ApiOperation("代币购买信用卡支付调用接口,返回支付界面")
    @GetMapping("/cardPayByToken")
    public String cardPayByToken(String number, String callBackUrl){
        return tokenOrderService.cardPayByToken(number, callBackUrl);
    }
}
