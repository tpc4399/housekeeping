package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.ReleaseRequirementADTO;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.dto.ReleaseRequirementCDTO;
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

    @ApiOperation("【客户】发布单次类型需求")
    @PostMapping("/A")
    public R releaseRequirementA(@RequestBody ReleaseRequirementADTO aDto){
        //查重

        //添加订单
        //入库

        //工作内容与订单挂钩 1:1
        //订单与时间段   1：n

        return R.ok();
    }

    @ApiOperation("【客户】发布定期服务类型需求")
    @PostMapping("/B")
    public R releaseRequirementB(@RequestBody ReleaseRequirementBDTO bDto){
        //添加订单
        //查重
        //入库

        return R.ok();
    }

    @ApiOperation("【客户】发布包月服务类型需求")
    @PostMapping("/C")
    public R releaseRequirementC(@RequestBody ReleaseRequirementCDTO cDto){
        //添加订单
        //查重
        //入库

        return R.ok();
    }

}
