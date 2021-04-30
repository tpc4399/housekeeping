package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.PaymentCallbackDTO;
import com.housekeeping.admin.dto.PaymentCallbackParams;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.service.IOrderDetailsService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.InvoiceObj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author su
 * @Date 2021/4/22 11:10
 */
@Api(tags={"【确认订单】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderDetails")
public class OrderDetailsController {

    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private AllInOne allInOne;

    @ApiOperation("【客户】确认订单——请求修改服务地址(需要保洁员同意才能修改成功)")
    @Access(RolesEnum.USER_CUSTOMER)
    @PostMapping("/requestToChangeAddress")
    public R requestToChangeAddress(RequestToChangeAddressDTO dto){
        return orderDetailsService.requestToChangeAddress(dto);
    }

    @ApiOperation("【保洁员】确认订单——同意修改地址")
    @Access(RolesEnum.USER_EMPLOYEES)
    @GetMapping("/agree/{id}")
    public R agree(@PathVariable Integer id){
        return orderDetailsService.requestToChangeAddressHandle(id, true);
    }

    @ApiOperation("【保洁员】确认订单——拒绝修改地址")
    @Access(RolesEnum.USER_EMPLOYEES)
    @GetMapping("/refuse/{id}")
    public R refuse(@PathVariable Integer id){
        return orderDetailsService.requestToChangeAddressHandle(id, false);
    }

    @ApiOperation("【客户】确认订单界面——支付操作需要调用")
    @Access(RolesEnum.USER_CUSTOMER)
    @PutMapping(value = "/pay", headers = "content-type=multipart/form-data")
    public R pay(@RequestParam("number") Long number,
                 @RequestParam("employeesId") Integer employeesId,
                 @RequestParam("photos") MultipartFile[] photos,
                 @RequestParam("evaluates") String[] evaluates,
                 @RequestParam("payType") String payType,
                 @RequestParam("remarks") String remarks) throws Exception {
        orderDetailsService.pay(number, employeesId, photos, evaluates, payType, remarks);
        return R.ok(null, "上传成功");
    }

    @ApiOperation("【用于三方支付】支付成功后的回调接口")
    @PostMapping("/paymentCallback")
    public String paymentCallback(PaymentCallbackParams params) throws IOException {
        PaymentCallbackDTO dto = new PaymentCallbackDTO(params);
        return orderDetailsService.paymentCallback(dto);
    }

    @ApiOperation("【测试】")
    @GetMapping("/test")
    public Long toBePaid(Long number, Integer employeesId){
        orderDetailsService.toBePaid(number, employeesId);
        return number;
    }

    @ApiOperation("【客户】信用卡支付调用接口")
    @GetMapping("/cardPay")
    public R cardPay(){
        allInOne.aioCheckOut(new Object(), new InvoiceObj());
        return R.ok();
    }

}
