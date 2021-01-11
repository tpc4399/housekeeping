package com.housekeeping.admin.controller;

import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
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

    @ApiOperation("发布服务去匹配保洁员，单次类型")
    public R releaseRequirementAToMatching(){
        return R.ok();
    }

    @ApiOperation("发布服务去匹配保洁员，定期服务类型")
    public R releaseRequirementBToMatching(){
        return R.ok();
    }

    @ApiOperation("发布服务去匹配保洁员，包月服务类型")
    public R releaseRequirementCToMatching(){
        return R.ok();
    }

}
