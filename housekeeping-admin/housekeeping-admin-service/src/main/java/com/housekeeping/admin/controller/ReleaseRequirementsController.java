package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.DemandDto;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.dto.ReleaseRequirementUDTO;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.admin.service.IReleaseRequirementService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
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
    private final ICustomerDetailsService customerDetailsService;

    @Access({RolesEnum.USER_CUSTOMER})
    @PostMapping
    @ApiOperation("【家庭端】需求发布接口")
    public R releaseRequirements(@RequestBody ReleaseRequirementBDTO dto) throws InterruptedException {
        return releaseRequirementService.releaseRequirements(dto);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @DeleteMapping
    @ApiOperation("【家庭端】删除已发布的需求")
    public R removedCusId(@RequestParam Integer id){
        return releaseRequirementService.removedCusId(id);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @PutMapping
    @ApiOperation("【家庭端】修改已发布的需求")
    public R removedCusId(@RequestBody ReleaseRequirementUDTO dto) throws InterruptedException {
        return releaseRequirementService.updateCus(dto);
    }

    @Access({RolesEnum.USER_COMPANY})
    @GetMapping("/getAllRequirementsByCompany")
    @ApiOperation("【公司端】获取所有未接需求")
    public R page(DemandDto demandDto,Page page){
        return releaseRequirementService.getAllRequirementsByCompany(demandDto,page);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @GetMapping("/getAllRequirements")
    @ApiOperation("【家庭端】获取所有已发布的需求")
    public R getAllReleaseRequirements(Page page){
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        return releaseRequirementService.getAllRequirement(cd.getId(), page);
    }

    @Access({RolesEnum.USER_CUSTOMER,RolesEnum.USER_COMPANY})
    @GetMapping("/getById")
    @ApiOperation("【家庭端】【公司端】根据id获取需求")
    public R getAllReleaseRequirements(Integer id){
        return releaseRequirementService.getCusById(id);
    }
}
