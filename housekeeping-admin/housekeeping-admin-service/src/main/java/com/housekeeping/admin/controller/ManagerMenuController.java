package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.UpdateManagerMenuDTO;
import com.housekeeping.admin.service.IManagerMenuService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @Date 2021/1/13 17:46
 */
@Api(tags = "【經理&菜單】中间表接口")
@AllArgsConstructor
@RestController
@RequestMapping("/managerMenu")
public class ManagerMenuController {

    private final IManagerMenuService managerMenuService;

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】修改经理权限")
    @PutMapping
    public R updateManagerMenu(@RequestBody UpdateManagerMenuDTO dto){
        return managerMenuService.updateManagerMenu(dto);
    }

    @Access({RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【经理】获取经理权限")
    @GetMapping
    public R getManagerMenu(Integer managerId){
        return managerMenuService.getManagerMenu(managerId);
    }

}
