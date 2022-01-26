package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @ApiOperation("【公司】【经理】根据经理id查看已参与的需求单")
    @GetMapping("/getInterestedByManager")
    public R getInterestedByManager(Integer managerId){
        return companyWorkListService.getInterestedByManager(managerId);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【经理】【员工】【個體戶】根据员工id查看已参与的需求单")
    @GetMapping("/getInterestedByEmp")
    public R getInterestedByEmp(Integer employeesId){
        return companyWorkListService.getInterestedByEmp(employeesId);
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
                               Integer  demandOrderId,
                               String empName){
        /* 根据客户需求,返回筛选后的员工ids */
        return companyWorkListService.suitableEmployees(userId,typeId,demandOrderId,empName);
    }


    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【经理】【员工】【個體戶】第三步：发送报价")
    @PostMapping("/newSendOffer")
    public R sendOffer(@RequestBody Map map){
        /* 经理或者公司选取员工，将员工添加到需求单感兴趣列表 */
        return companyWorkListService.newSendOffer(map);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("(新)【公司】【经理】【员工】【個體戶】第三步：发送报价")
    @PostMapping("/newSendOffer2")
    public R newSendOffer2(@RequestBody Map map){
        /* 经理或者公司选取员工，将员工添加到需求单感兴趣列表 */
        return companyWorkListService.newSendOffer2(map);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【经理】【员工】【個體戶】修改报价单价格")
    @PostMapping("/newChangePrice")
    public R changePrice(@RequestBody Map map){
        return companyWorkListService.newChangePrice(map);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("（新）【公司】【经理】【员工】【個體戶】修改报价单价格")
    @PostMapping("/newChangePrice2")
    public R newChangePrice2(@RequestBody Map map){
        return companyWorkListService.newChangePrice2(map);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【经理】【员工】【個體戶】第二步：选择合适的保洁员")
    @GetMapping("/selectSuitableEmployees")
    public R selectSuitableEmployees(@RequestParam Integer employeesId, @RequestParam Integer demandOrderId,
                                     @RequestParam(required = false) List<Integer> attendant){
        /* 经理或者公司选取员工，将员工添加到需求单感兴趣列表 */
        return companyWorkListService.selectSuitableEmployees(employeesId, demandOrderId,attendant);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【经理】【员工】【個體戶】修改报价单价格")
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

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【经理】【员工】【個體戶】删除报价单")
    @DeleteMapping("/remove")
    public R remove(@RequestParam Integer id){
        return companyWorkListService.cusRemove(id);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【公司】【经理】【员工】【個體戶】根据id获取报价单")
    @GetMapping("/getQuotationById")
    public R cusGetByid(@RequestParam Integer quotationId){
        return R.ok(companyWorkListService.cusGetById(quotationId));
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】获取所有报价单")
    @GetMapping("/getAllQuotationByAdmin")
    public R getAllQuotationByAdmin(Page page){
        return companyWorkListService.getAllQuotationByAdmin(page);
    }

}
