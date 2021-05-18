package com.housekeeping.admin.controller;

import com.housekeeping.admin.entity.PaymentCallback;
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
import java.time.LocalDateTime;

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

    @ApiOperation("【客户】确认订单界面——支付操作需要调用,订单状态———— 未支付->处理中")
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


    @Access(RolesEnum.USER_EMPLOYEES)
    @ApiOperation("【保洁员】【客户】订单作废")
    @GetMapping("/payment1")
    public R payment1(String number){
        //调用“订单作废”服务
        return orderDetailsService.inputSql(number, false);
    }

    @Access(RolesEnum.USER_EMPLOYEES)
    @ApiOperation("【保洁员】订单状态———— 处理中->未支付")
    @GetMapping("/payment2")
    public R payment2(String number){
        //调“回转为未支付状态”的服务
        return orderDetailsService.payment2(number);
    }

    @ApiOperation("【用于三方支付】支付成功后的回调接口,订单状态———— 处理中->已支付")
    @PostMapping("/paymentCallback")
    public String paymentCallback(PaymentCallbackParams params) throws IOException {
        System.out.println("PaymentCallbackParams:" + LocalDateTime.now() + "  " + params.toString());
        PaymentCallback pc = new PaymentCallback(params);
        orderDetailsService.paymentCallback(pc);
        return "OK";
    }

    @Access(RolesEnum.USER_EMPLOYEES)
    @ApiOperation("【保洁员】订单状态———— 进行中->待评价")
    @GetMapping("/payment3")
    public R payment3(String number){
        return orderDetailsService.payment3(number);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】评价订单———— 待评价->已完成  待做")
    @GetMapping("/payment4")
    public R payment4(){
        return R.ok();
    }

    @Access(RolesEnum.USER_EMPLOYEES)
    @ApiOperation("【保洁员】订单误判———— 已取消订单->待服务 待做")
    @GetMapping("/payment5")
    public R payment5(){
        return R.ok();
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/queryByCus")
    public R queryByCus(Integer type){
        return orderDetailsService.queryByCus(type);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【保洁员】订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/queryByEmp")
    public R queryByEmp(Integer type){
        return orderDetailsService.queryByEmp(type);
    }

    @ApiOperation("【测试】")
    @GetMapping("/test")
    public R toBePaid(String number){
        return orderDetailsService.inputSql(number, true);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】信用卡支付调用接口")
    @GetMapping("/cardPay")
    public R cardPay(){
        allInOne.aioCheckOut(new Object(), new InvoiceObj());
        return R.ok();
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理員】根據訂單number獲取訂單狀態")
    @GetMapping("/getState")
    public R getState(String number){
        return R.ok(orderDetailsService.getState(number), "成功獲取訂單狀態");
    }

    @Access({RolesEnum.SYSTEM_ADMIN,RolesEnum.USER_COMPANY,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_CUSTOMER,RolesEnum.USER_MANAGER})
    @ApiOperation("【管理員】【公司】【保洁员】【客户】【经理】根據訂單number獲取訂單详情")
    @GetMapping("/getOrder")
    public R getOrder(String number){
        return R.ok(orderDetailsService.getOrder(number), "成功獲取訂單详情");
    }

}
