package com.housekeeping.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutCVS;
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
@Api(tags={"【信用卡支付接口】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    public static AllInOne all;

    @ApiOperation("三方支付接口测试")
    @GetMapping
    public String paymentHandle(){
        initial();
        return genAioCheckOutCVS();
    }

    private void initial(){
        all = new AllInOne("");
    }
    public String genAioCheckOutCVS(){
        AioCheckOutCVS obj = new AioCheckOutCVS();
        InvoiceObj invoice = new InvoiceObj();
        UUID uid = UUID.randomUUID();
        obj.setMerchantTradeNo(uid.toString().replaceAll("-", "").substring(0, 20));
        obj.setMerchantTradeDate("2017/01/01 08:05:23");
        obj.setTotalAmount("50");
        obj.setTradeDesc("test Description");
        obj.setItemName("TestItem");
        obj.setReturnURL("http://211.23.128.214:5000");
        obj.setNeedExtraPaidInfo("N");
        obj.setStoreExpireDate("3");
        obj.setInvoiceMark("Y");
        invoice.setRelateNumber("test202017test");
        invoice.setCustomerID("123456");
        invoice.setCarruerType("1");
        invoice.setTaxType("1");
        invoice.setCarruerNum("");
        invoice.setDonation("0");
        invoice.setLoveCode("X123456");
        invoice.setPrint("0");
        invoice.setCustomerName("Mark");
        invoice.setCustomerAddr("台北市南港區三重路");
        invoice.setCustomerPhone("0911429215");
        invoice.setDelayDay("1");
        invoice.setInvType("07");
        invoice.setInvoiceItemName("測試");
        invoice.setInvoiceItemCount("1");
        invoice.setInvoiceItemWord("個");
        invoice.setInvoiceItemPrice("50");
        invoice.setInvoiceItemTaxType("1");
        String form = all.aioCheckOut(obj, invoice);
        return form;
    }
}
