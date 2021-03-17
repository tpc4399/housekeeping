package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.service.IReleaseRequirementService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

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

    @Access({RolesEnum.USER_CUSTOMER})
    @PostMapping
    @ApiOperation("【家庭端】需求发布接口")
    public R releaseRequirements(@RequestBody ReleaseRequirementBDTO dto) throws InterruptedException {
        return releaseRequirementService.releaseRequirements(dto);
    }

    @Access({RolesEnum.USER_COMPANY})
    @GetMapping
    @ApiOperation("【公司端】获取所有需求")
    public R page(IPage page){
        return releaseRequirementService.page(page);
    }

}
