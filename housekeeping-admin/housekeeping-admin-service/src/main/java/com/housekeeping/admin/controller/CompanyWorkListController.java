package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Delete;
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

    /*@Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】第一步：感興趣操作接口")
    @GetMapping("/beInterested/{demandOrderId}")
    public R beInterested(@PathVariable Integer demandOrderId){
        *//* 添加需求订单到感兴趣列表 *//*
        return companyWorkListService.beInterested(demandOrderId);
    }*/

    @Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】查看已参与的需求单")
    @GetMapping("/getInterestedByCompany")
    public R getInterestedByCompany(){
        return companyWorkListService.getInterestedByCompany();
    }

    @Access(RolesEnum.USER_MANAGER)
    @ApiOperation("【经理】查看已参与的需求单")
    @GetMapping("/getInterestedByManager")
    public R getInterestedByManager(){
        return companyWorkListService.getInterestedByManager();
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】查看感兴趣的报价单列表")
    @GetMapping("/getAllInterestedEmployees")
    public R getAllInterestedEmployees(@RequestParam Integer demandOrderId){
        return companyWorkListService.getAllInterestedEmployees(demandOrderId);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】第一步：获取保洁员(type 0公司 1经理)")
    @GetMapping("/suitableEmployees")
    public R suitableEmployees(@RequestParam Integer userId,
                               @RequestParam Integer typeId,
                               Integer  demandOrderId){
        /* 根据客户需求,返回筛选后的员工ids */
        return companyWorkListService.suitableEmployees(userId,typeId,demandOrderId);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】第二步：选择合适的保洁员")
    @GetMapping("/selectSuitableEmployees")
    public R selectSuitableEmployees(@RequestParam Integer employeesId,@RequestParam Integer demandOrderId){
        /* 经理或者公司选取员工，将员工添加到需求单感兴趣列表 */
        return companyWorkListService.selectSuitableEmployees(employeesId, demandOrderId);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】修改报价单价格")
    @GetMapping("/changePrice")
    public R changePrice(@RequestParam String quotationId,@RequestParam Integer price){
        return companyWorkListService.changePrice(quotationId,price);
    }


    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】确定报价，将其添加到待付款订单")
    @GetMapping("/confirmDemand")
    public R confirmDemand(@RequestParam Integer quotationId){
        return companyWorkListService.confirmDemand(quotationId);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】删除报价单")
    @DeleteMapping("/remove")
    public R remove(@RequestParam Integer id){
        return companyWorkListService.cusRemove(id);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】根据id获取报价单")
    @GetMapping("/getQuotationById")
    public R cusGetByid(@RequestParam Integer quotationId){
        return R.ok(companyWorkListService.cusGetById(quotationId));
    }

}
