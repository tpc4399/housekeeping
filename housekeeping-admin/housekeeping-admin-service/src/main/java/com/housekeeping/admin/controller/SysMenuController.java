package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.entity.SysMenu;
import com.housekeeping.admin.service.ISysMenuService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author su
 * @Date 2021/1/14 15:06
 */
@Api(tags = "【菜單】接口")
@AllArgsConstructor
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {

    private final ISysMenuService sysMenuService;

    @ApiOperation("獲取經歷可能的所有的菜單名稱")
    @GetMapping
    public R getMenusManager(){
        QueryWrapper qw = new QueryWrapper();
        qw.le("id", 3999);
        qw.ge("id", 1000);
        List<SysMenu> sysMenus = sysMenuService.list(qw);
        return R.ok(sysMenus, "查詢成功");
    }

    @ApiOperation("獲取菜單名稱")
    @GetMapping("/getMenu/{id}")
    public R getMenuById(@PathVariable Integer id){
        SysMenu sysMenu = sysMenuService.getById(id);
        if (CommonUtils.isNotEmpty(sysMenu)){
            return R.ok(sysMenu, "獲取成功");
        }else {
            return R.failed("該菜單不存在");
        }
    }

}
