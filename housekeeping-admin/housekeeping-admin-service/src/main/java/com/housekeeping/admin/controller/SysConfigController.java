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
        return R.ok(sysConfigService.list(), "查询成功");
    }

    @GetMapping("/config")
    @ApiOperation("【管理员】设置配置信息")
    public R config(String key, String value, String description){
        QueryWrapper qw = new QueryWrapper();
        qw.eq("config_key", key);
        List<SysConfig> sysConfigList = sysConfigService.list();
        SysConfig config = sysConfigService.getOne(qw);
        if (CommonUtils.isEmpty(config)){
            sysConfigService.save(new SysConfig(null, key, value, description));
        }else {
            if (value.equals(config.getConfigValue())){
                //还是这个值，不用管
            }else {
                sysConfigService.updateById(new SysConfig(config.getId(), key, value, description));
            }
        }
        return R.ok("设置成功");
    }

}
