package com.housekeeping.admin.controller;


import com.housekeeping.admin.dto.SysOrderContractorDTO;
import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.service.ISysOrderContractorService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/11/17 21:22
 */
@Api(value="订单controller",tags={"【包工订单计划】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysOrderContractor")
public class SysOrderContractorController {

    private final ISysOrderContractorService sysOrderContractorService;

    @ApiOperation("【客户】订单计划创建（发布需求）")
    @PostMapping
    public R releaseOrderPlan(@RequestBody SysOrderContractorDTO sysOrderPlanDTO) {
        return sysOrderContractorService.releaseOrderContractor(sysOrderPlanDTO);
    }

}
