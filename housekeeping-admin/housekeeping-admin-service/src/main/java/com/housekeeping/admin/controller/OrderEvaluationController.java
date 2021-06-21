package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.admin.service.IOrderEvaluationService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2021/6/7 9:26
 */
@Api(tags={"【订单双向评价】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderEvaluation")
public class OrderEvaluationController {

    private final IOrderEvaluationService orderEvaluationService;

    @ApiOperation("【保洁员】【客户】【公司】【经理】评价某个订单")
    @Access({RolesEnum.USER_EMPLOYEES, RolesEnum.USER_CUSTOMER,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @PostMapping
    public R evaluation(@RequestBody OrderEvaluationDTO dto){
        return orderEvaluationService.evaluation(dto);
    }

    @ApiOperation("【管理員】【公司】【經理】【保洁员】【客户】查看某个订单的評價")
    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_EMPLOYEES, RolesEnum.USER_CUSTOMER, RolesEnum.USER_MANAGER})
    @GetMapping
    public R getEvaluation(String orderNumber){
        return orderEvaluationService.getEvaluation(orderNumber);
    }

}
