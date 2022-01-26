package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.service.ISysOrderPlanService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.BrokenBarrierException;


/**
 * @Author su
 * @create 2020/11/16 14:01
 */
@Api(value="订单controller",tags={"【订单计划】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysOrderPlan")
public class SysOrderPlanController {

    private final ISysOrderPlanService sysOrderPlanService;

//    @ApiOperation("【客户】订单计划创建（发布需求）")
//    @PostMapping
//    public R releaseOrderPlan(@RequestBody SysOrderPlanDTO sysOrderPlanDTO) throws BrokenBarrierException, InterruptedException {
//        return sysOrderPlanService.releaseOrderPlan(sysOrderPlanDTO);
//    }

}
