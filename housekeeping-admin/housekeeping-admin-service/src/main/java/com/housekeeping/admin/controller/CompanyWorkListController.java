package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 1 客户发布需求
 * 2公司看到之后，
 * 3公司可以点感兴趣，要加入一名保潔員
 * 4公司可以和客戶聊天，就会有公司，经理，保洁员在里面
 * 5客戶點擊選擇了的保健員
 * 6系統生成「臨時」訂單
 * 7公司這個時候可以修改價錢和服務細則
 * 8客戶支付，建立真正的訂單和排班
 * @Author su
 * @create 2020/11/18 16:12
 */
@Api(value="公司controller",tags={"【感興趣】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyWorkList")
public class CompanyWorkListController {

    private final ICompanyWorkListService companyWorkListService;

    @Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】第一步：感興趣操作接口")
    @GetMapping("/beInterested/{demandOrderId}")
    public R beInterested(@PathVariable Integer demandOrderId){
        /* 添加需求订单到感兴趣列表 */
        return companyWorkListService.beInterested(demandOrderId);
    }

    @Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】第二步：获取合适的保洁员")
    @GetMapping("/suitableEmployees/{demandOrderId}")
    public R suitableEmployees(@PathVariable Integer demandOrderId){
        /* 生成每个员工的临时订单，返回筛选后的员工ids */
        return companyWorkListService.suitableEmployees(demandOrderId);
    }

    @Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】第三步：选择合适的保洁员")
    @GetMapping("/selectSuitableEmployees")
    public R selectSuitableEmployees(String employeesId, Integer demandOrderId){
        /* 存储该保洁员的的临时订单 */
        return companyWorkListService.selectSuitableEmployees(employeesId, demandOrderId);
    }

    @Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】第四步：针对这个需求订单发起聊天")
    @GetMapping("/initiateChat/{demandOrderId}")
    public R initiateChat(@PathVariable String demandOrderId){
        /* 发起聊天 */
        return companyWorkListService.initiateChat(demandOrderId);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】请求发送临时订单")
    @GetMapping("/requestToSendTemporaryOrder")
    public R requestToSendTemporaryOrder(Integer demandOrderId, Integer companyId){
        return companyWorkListService.requestToSendTemporaryOrder(demandOrderId, companyId);
    }

}
