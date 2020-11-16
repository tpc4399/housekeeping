package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.service.ISysOrderPlanService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author su
 * @create 2020/11/16 14:01
 */
@Api(value="订单controller",tags={"【顾客】订单计划详细接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysOrderPlan")
public class SysOrderPlanController {

    private final ISysOrderPlanService sysOrderPlanService;

    @PostMapping
    public R releaseOrderPlan(@RequestBody SysOrderPlanDTO sysOrderPlanDTO){
        return R.ok(sysOrderPlanService.releaseOrderPlan(sysOrderPlanDTO));
    }

}
