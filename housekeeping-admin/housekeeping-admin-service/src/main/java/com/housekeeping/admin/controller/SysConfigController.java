package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.entity.SysConfig;
import com.housekeeping.admin.service.ISysConfigService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author su
 * @Date 2021/2/23 10:49
 */
@Api(tags={"【系统配置】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysConfig")
public class SysConfigController {

    private final ISysConfigService sysConfigService;

    @GetMapping
    @ApiOperation("【管理员】获取所有系统配置")
    public R getAll(){
        return R.ok(sysConfigService.getOne(new QueryWrapper<>()), "查询成功");
    }

    @PutMapping
    @ApiOperation("【管理员】设置配置信息")
    public R config(@RequestBody SysConfig sysConfig){
        List<SysConfig> sysConfigList = sysConfigService.list();
        if (sysConfigList.size() == 0){
            sysConfigService.save(sysConfig);
        }else if (sysConfigList.size() == 1){
            sysConfigService.update(sysConfig, new QueryWrapper<>());
        }else if (sysConfigList.size() > 1){
            sysConfigService.remove(new QueryWrapper<>());
            sysConfigService.save(sysConfig);
        }
        return R.ok("设置成功");
    }

}
