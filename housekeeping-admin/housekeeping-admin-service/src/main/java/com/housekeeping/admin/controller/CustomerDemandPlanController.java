package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICustomerDemandPlanService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2020/12/29 9:46
 */
@Api(tags={"【客户需求计划】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/customerDemandPlan")
public class CustomerDemandPlanController {

    private final ICustomerDemandPlanService customerDemandPlanService;

//    @GetMapping("/setJobContends")
//    public R setJobContends(Integer id, Integer jobsId){
//        return customerDemandPlanService.setJobContends(id, jobsId);
//    }

}
