package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ISysAddressAreaService;
import com.housekeeping.admin.service.ISysAddressCityService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2021/3/8 16:59
 */
@Api(tags={"【台湾地址】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/address")
public class SysAddressController {

    private final ISysAddressCityService sysAddressCityService;
    private final ISysAddressAreaService sysAddressAreaService;

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES, RolesEnum.USER_CUSTOMER})
    @ApiOperation("【all】获取所有市")
    @GetMapping("/city")
    public R city(){
        return sysAddressCityService.getAll();
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES, RolesEnum.USER_CUSTOMER})
    @ApiOperation("【all】获取市里所有区")
    @GetMapping("/areaByCityId")
    public R areaByCityId(Integer cityId){
        return sysAddressAreaService.getAllByCityId(cityId);
    }

}
