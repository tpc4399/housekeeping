package com.housekeeping.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutCVS;
import ecpay.payment.integration.domain.AioCheckOutOneTime;
import ecpay.payment.integration.domain.InvoiceObj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Author su
 * @create 2021/5/13 10:51
 */
@Api(tags={"【信用卡支付】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    public static AllInOne all;

    @ApiOperation("三方支付接口测试")
    @GetMapping
    public String paymentHandle(){
        initial();
        return genAioCheckOutOneTime();
    }

    private void initial(){
        all = new AllInOne("");
    }

    //信用卡一次性消费
    public static String genAioCheckOutOneTime(){
        AioCheckOutOneTime obj = new AioCheckOutOneTime();
        UUID uid = UUID.randomUUID();
        obj.setMerchantTradeNo(uid.toString().replaceAll("-", "").substring(0, 20));
        obj.setMerchantTradeDate("2017/01/01 08:05:23");
        obj.setTotalAmount("500");
        obj.setTradeDesc("test Description");
        obj.setItemName("TestItem");
        obj.setReturnURL("http://211.23.128.214:5000");
        obj.setNeedExtraPaidInfo("N");
        obj.setRedeem("Y");
        String form = all.aioCheckOut(obj, null);
        return form;
    }

    /* 返回信用卡一次性消费页面，家政服务 */
    public static String oneTimeConsumptionOfCreditCard(){
        return "";
    }
}
