package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.OrderRefundDTO;
import com.housekeeping.admin.entity.AgreeRefund;
import com.housekeeping.admin.service.IOrderRefundService;
import com.housekeeping.admin.vo.AgreeRefundByFinance;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * @Author su
 * @Date 2020/12/11 16:09
 */
@Api(tags={"【退款】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderRefund")
public class OrderRefundController {

    private final IOrderRefundService orderRefundService;

    @ApiOperation("【客户】客户发起退款")
    @Access(RolesEnum.USER_CUSTOMER)
    @PostMapping("/requireRefund")
    public R requireRefund(@RequestBody OrderRefundDTO orderRefundDTO){
        return orderRefundService.requireRefund(orderRefundDTO);
    }

    @ApiOperation("【客户】取消退款")
    @Access(RolesEnum.USER_CUSTOMER)
    @PostMapping("/cancelRefund")
    public R requireRefund(@RequestParam Integer id){
        return orderRefundService.cancelRefund(id);
    }

    @ApiOperation("【客户】客戶查看所有已發起的退款申請")
    @Access(RolesEnum.USER_CUSTOMER)
    @GetMapping("/getAllRefund")
    public R getAllRefund(){
        return R.ok(orderRefundService.getAllRefund());
    }

    @ApiOperation("【客户】【公司】【經理】【員工】根據id查看退款申請")
    @Access({RolesEnum.USER_CUSTOMER,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_EMPLOYEES})
    @GetMapping("/getRefundById")
    public R getAllRefund(Integer id){
        return orderRefundService.getRefundById(id);
    }



    @ApiOperation("【公司】公司查看退款申請")
    @Access(RolesEnum.USER_COMPANY)
    @GetMapping("/getRefundByCom")
    public R getRefundByCom(){
        return R.ok(orderRefundService.getRefundByCom());
    }

    @ApiOperation("【公司】【经理】经理查看退款申請")
    @Access({RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY})
    @GetMapping("/getRefundByMan")
    public R getRefundByMan(@RequestParam Integer manId){
        return R.ok(orderRefundService.getRefundByMan(manId));
    }

    @ApiOperation("【公司】【经理】【员工】员工查看退款申請")
    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY})
    @GetMapping("/getRefundByEmp")
    public R getRefundByEmp(@RequestParam Integer empId){
        return R.ok(orderRefundService.getRefundByEmp(empId));
    }


    @ApiOperation("根据userId查看退款申请")
    @GetMapping("/getByUserId")
    public R getByUserId(@RequestParam Integer userId){
        return R.ok(orderRefundService.getByUserId(userId));
    }

    @ApiOperation("【公司】【经理】【员工】拒絕退款申請")
    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY})
    @PostMapping("/refuseRefund")
    public R handleRefund(@RequestBody AgreeRefund agreeRefund){
        return orderRefundService.refuseRefund(agreeRefund);
    }

    @ApiOperation("【公司】【经理】【员工】同意退款申請")
    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY})
    @PostMapping("/agreeRefund")
    public R agreeRefund(@RequestBody AgreeRefund agreeRefund){
        return orderRefundService.agreeRefund(agreeRefund);
    }

    @ApiOperation("財務查看所有公司同意退款的列表")
    @PostMapping("/getAllAgreeRefund")
    public R getAllAgreeRefund(){
        return orderRefundService.getAllAgreeRefund();
    }

    @ApiOperation("【公司】【经理】【员工】財務拒絕退款申請")
    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY})
    @PostMapping("/refuseRefundByFinance")
    public R getAllAgreeRefund(@RequestParam Integer id){
        return orderRefundService.refuseRefundByFinance(id);
    }

    @ApiOperation("【公司】【经理】【员工】財務同意退款申請")
    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_MANAGER,RolesEnum.USER_COMPANY})
    @PostMapping("/agreeRefundByFinance")
    public R agreeRefundByFinance(@RequestBody AgreeRefundByFinance agreeRefund){
        return orderRefundService.agreeRefundByFinance(agreeRefund);
    }


}
