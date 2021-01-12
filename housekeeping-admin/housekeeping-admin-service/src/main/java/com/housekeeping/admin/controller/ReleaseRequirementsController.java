package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.ReleaseRequirementADTO;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.dto.ReleaseRequirementCDTO;
import com.housekeeping.admin.service.IReleaseRequirementService;
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
 * @Date 2021/1/5 11:30
 */
@Api(tags={"【需求发布】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/releaseRequirements")
public class ReleaseRequirementsController {

    private final IReleaseRequirementService releaseRequirementService;

    @ApiOperation("【客户】发布单次类型需求")
    @PostMapping("/A")
    public R releaseRequirementA(@RequestBody ReleaseRequirementADTO aDto){
        //工作内容查重
        R ra = releaseRequirementService.jobContendRecheckingA(aDto.getRulesDateVos());
        if(ra.getCode() == 1){
            return ra;
        }
        //添加订单
        releaseRequirementService.generateOrder(aDto.getSonIds(), 1);
        //入库
        releaseRequirementService.putInStorageA(aDto.getRulesDateVos());
        return R.ok("成功發佈需求");
    }

    @ApiOperation("【客户】发布定期服务类型需求")
    @PostMapping("/B")
    public R releaseRequirementB(@RequestBody ReleaseRequirementBDTO bDto){
        //工作内容查重
        R rb = releaseRequirementService.jobContendRecheckingB(bDto.getRulesWeekVos());
        if(rb.getCode() == 1){
            return rb;
        }
        //添加订单
        releaseRequirementService.generateOrder(bDto.getSonIds(), 2);
        //入库
        releaseRequirementService.putInStorageB(bDto.getRulesWeekVos());
        return R.ok("成功發佈需求");
    }

    @ApiOperation("【客户】发布包月服务类型需求")
    @PostMapping("/C")
    public R releaseRequirementC(@RequestBody ReleaseRequirementCDTO cDto){
        //工作内容查重
        R rc = releaseRequirementService.jobContendRecheckingC(cDto.getRulesMonthlyVo());
        if(rc.getCode() == 1){
            return rc;
        }
        //添加订单
        releaseRequirementService.generateOrder(cDto.getSonIds(), 3);
        //入库
        releaseRequirementService.putInStorageC(cDto.getRulesMonthlyVo());
        return R.ok("成功發佈需求");
    }

}
