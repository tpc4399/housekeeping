package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.PaymentCallback;
import com.housekeeping.admin.service.ICardPayCallbackService;
import com.housekeeping.admin.service.IOrderDetailsService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
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
@Api(tags={"【确认订单&订单列表】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderDetails")
public class OrderDetailsController {

    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private ICardPayCallbackService cardPayCallbackService;

    @ApiOperation("【客户】确认订单——请求修改服务地址(需要保洁员同意才能修改成功)")
    @PostMapping("/requestToChangeAddress")
    public R requestToChangeAddress(RequestToChangeAddressDTO dto){
        return orderDetailsService.requestToChangeAddress(dto);
    }

    @ApiOperation("【保洁员】确认订单——同意修改地址")
    @GetMapping("/agree/{id}")
    public R agree(@PathVariable Integer id){
        return orderDetailsService.requestToChangeAddressHandle(id, true);
    }

    @ApiOperation("【保洁员】确认订单——拒绝修改地址")
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


    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_CUSTOMER,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【客户】【公司】【經理】【个体户】订单作废")
    @GetMapping("/payment1")
    public R payment1(String number){
        //调用“订单作废”服务
        return orderDetailsService.inputSql(number, false);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【个体户】订单状态———— 处理中->未支付")
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

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【公司】【经理】【个体户】订单状态———— 待服務->進行中")
    @GetMapping("/payment7")
    public R payment7(String number){
        return orderDetailsService.payment7(number);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【公司】【经理】【个体户】订单状态———— 进行中->待评价")
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

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保潔員】【个体户】评价订单———— 待评价->已完成  待做")
    @GetMapping("/payment6")
    public R payment6(){
        return R.ok();
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【个体户】订单误判———— 已取消订单->待服务 待做")
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

    @ApiOperation("本公司订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/queryByCom")
    public R queryByCom(Integer type){
        return orderDetailsService.queryByCom(type);
    }

    @ApiOperation("(新)本公司订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/newQueryByCom")
    public R newQueryByCom(Integer type){
        return orderDetailsService.newQueryByCom(type);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【个体户】指定本公司某保潔員的訂單查詢 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/queryEmpByCom")
    public R queryEmpByCom(Integer type, Integer employeesId){
        return orderDetailsService.queryEmpByCom(type, employeesId);
    }

    @ApiOperation("【公司】公司查看某个经理旗下的保洁员订单 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/queryByManagerId")
    public R queryByManager(@RequestParam Integer manId,@RequestParam Integer type){
        return orderDetailsService.queryByManagerId(manId,type);
    }

    @Access(RolesEnum.USER_MANAGER)
    @ApiOperation("【經理】自己旗下保潔員的訂單查詢 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/queryByManager")
    public R queryByManager(Integer type){
        return orderDetailsService.queryByManager(type);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成")
    @GetMapping("/queryByAdmin")
    public R queryByAdmin(Integer type, Page page){
        return orderDetailsService.queryByAdmin(type,page);
    }

    @Access(RolesEnum.USER_EMPLOYEES)
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

    @ApiOperation("【客户】信用卡支付调用接口,返回支付界面")
    @GetMapping("/cardPay")
    public String cardPay(String number, String callBackUrl){
        return orderDetailsService.cardPay(number, callBackUrl);
    }

    @ApiOperation("【客户】信用卡支付結果回調接口，獲取回調數據")
    @PostMapping("/cardPayCallback")
    public String cardPayCallback(@RequestBody CardPayCallbackParams params){
        return orderDetailsService.cardPayCallback(params);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理員】根據訂單number獲取訂單狀態")
    @GetMapping("/getState")
    public R getState(String number){
        return R.ok(orderDetailsService.getState(number), "成功獲取訂單狀態");
    }

    @Access({RolesEnum.SYSTEM_ADMIN,RolesEnum.USER_COMPANY,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_CUSTOMER,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【管理員】【公司】【保洁员】【客户】【经理】【个体户】根據訂單number獲取訂單详情")
    @GetMapping("/getOrder")
    public R getOrder(String number){
        return R.ok(orderDetailsService.getOrder(number), "成功獲取訂單详情");
    }

    @Access({RolesEnum.SYSTEM_ADMIN,RolesEnum.USER_COMPANY,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_CUSTOMER,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【管理員】【公司】【保洁员】【客户】【经理】【个体户】(新)根據訂單number獲取訂單详情")
    @GetMapping("/getOrder2")
    public R getOrder2(String number){
        return R.ok(orderDetailsService.getOrder2(number), "成功獲取訂單详情");
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】修改待支付订单的工作安排")
    @PostMapping("/setWorkDetails")
    public R setWorkDetails(@RequestBody SetOrderWorkDetailsDTO dto){
        return orderDetailsService.setWorkDetails(dto);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】修改待支付订单的工作内容")
    @PostMapping("/setJobs")
    public R setJobs(@RequestBody SetOrderJobsDTO dto){
        return orderDetailsService.setJobs(dto);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】修改待支付订单的折后价格")
    @PostMapping("/setDiscountPrice")
    public R setDiscountPrice(@RequestBody SetOrderDiscountPriceDTO dto){
        return orderDetailsService.setDiscountPrice(dto);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】修改待支付订单的客户备注")
    @PostMapping("/setNote")
    public R setNote(@RequestParam("number") Long number,
                     @RequestParam("photos") MultipartFile[] photos,
                     @RequestParam("evaluates") String[] evaluates,
                     @RequestParam("remarks") String remarks){
        return orderDetailsService.setNote(number,photos,evaluates,remarks);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】修改待支付订单的客戶信息")
    @PostMapping("/setCustomerInformation")
    public R setCustomerInformation(@RequestBody SetOrderCustomerInformationDTO dto){
        return orderDetailsService.setCustomerInformation(dto);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保潔員】【公司】【经理】【个体户】修改待支付订单")
    @PostMapping("/setOrderInformation")
    public R setOrderInformation(@RequestBody SetOrderInformationDTO dto){
        return orderDetailsService.setOrderInformation(dto);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("(新)【客戶】獲取工作時間表")
    @PostMapping("/getWorkTimeTableByCus")
    public R getWorkTimeTableByCus(@RequestBody TimeTableByCusDTO dto){
        return orderDetailsService.getWorkTimeTableByCus(dto);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY,RolesEnum.USER_PERSONAL})
    @ApiOperation("(新)【保潔員】【经理】【公司】【个体户】獲取员工工作時間表")
    @PostMapping("/getWorkTimeTableByEmp")
    public R getWorkTimeTableByEmp(@RequestBody TimeTableByEmpDTO dto){
        return orderDetailsService.getWorkTimeTableByEmp(dto);
    }

    @Access({RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY})
    @ApiOperation("(新)【經理】【公司】獲取经理工作時間表")
    @PostMapping("/getWorkTimeTableByMan")
    public R getWorkTimeTableByMan(@RequestBody TimeTableByManDTO dto){
        return orderDetailsService.getWorkTimeTableByMan(dto);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_PERSONAL})
    @ApiOperation("(新)【公司】【个体户】獲取公司工作時間表")
    @PostMapping("/getWorkTimeTableByCom")
    public R getWorkTimeTableByCom(@RequestBody TimeTableByComDTO dto){
        return orderDetailsService.getWorkTimeTableByCom(dto);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_CUSTOMER,RolesEnum.USER_PERSONAL})
    @ApiOperation("(新)【客戶】【保潔員】【經理】【公司】【个体户】獲取具体工作時間内容")
    @PostMapping("/getWorkTimeDetails")
    public R getWorkTimeDetails(@RequestParam Integer id){
        return orderDetailsService.getWorkTimeDetails(id);
    }

    @Access({RolesEnum.SYSTEM_ADMIN,RolesEnum.USER_COMPANY,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_CUSTOMER,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【管理員】【公司】【保洁员】【客户】【经理】【个体户】(新)根據訂單number獲取訂單退款信息")
    @GetMapping("/getRefundByNumber")
    public R getRefundByNumber(String number){
        return orderDetailsService.getRefundByNumber(number);
    }

    @Access({RolesEnum.SYSTEM_ADMIN,RolesEnum.USER_COMPANY,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_CUSTOMER,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【管理員】【公司】【保洁员】【客户】【经理】【个体户】(新)根據訂單number獲取代币訂單详情")
    @GetMapping("/getTokenOrder")
    public R getTokenOrder(@RequestParam String number,
                           @RequestParam Integer type){
        return orderDetailsService.getTokenOrder(number,type);
    }


}
