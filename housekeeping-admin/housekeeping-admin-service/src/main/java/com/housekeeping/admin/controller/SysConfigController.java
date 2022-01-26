package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.dto.WeightDTO;
import com.housekeeping.admin.entity.SysConfig;
import com.housekeeping.admin.service.ISysConfigService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.ApplicationConfigConstants;
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

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping
    @ApiOperation("【管理员】获取所有系统配置")
    public R getAll(){
        return R.ok(sysConfigService.list(), "查询成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
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

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/matchmakingFeeSwitchBoolean")
    @ApiOperation("【管理员】设置媒合費开关")
    public R matchmakingFeeSwitchBoolean(String value){
        String key = ApplicationConfigConstants.matchmakingFeeSwitchBoolean;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/matchmakingFeeFloat")
    @ApiOperation("【管理员】设置媒合費")
    public R matchmakingFeeFloat(String value){
        String key = ApplicationConfigConstants.matchmakingFeeFloat;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/systemServiceFeeSwitchBoolean")
    @ApiOperation("【管理员】设置系統服務費開關")
    public R systemServiceFeeSwitchBoolean(String value){
        String key = ApplicationConfigConstants.systemServiceFeeSwitchBoolean;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/systemServiceFeeFloat")
    @ApiOperation("【管理员】设置系統服務費")
    public R systemServiceFeeFloat(String value){
        String key = ApplicationConfigConstants.systemServiceFeeFloat;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/servicesChargeForCreditCardFloat")
    @ApiOperation("【管理员】设置刷卡手續費百分比")
    public R servicesChargeForCreditCardFloat(String value){
        String key = ApplicationConfigConstants.servicesChargeForCreditCardFloat;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/servicesChargeForCreditCardSwitchBoolean")
    @ApiOperation("【管理员】设置刷卡手續費开关")
    public R servicesChargeForCreditCardSwitchBoolean(String value){
        String key = ApplicationConfigConstants.servicesChargeForCreditCardSwitchBoolean;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/numberOfConsecutiveCompanyInteger")
    @ApiOperation("【管理员】设置连续公司数量")
    public R numberOfConsecutiveCompanyInteger(String value){
        String key = ApplicationConfigConstants.numberOfConsecutiveCompanyInteger;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/numberOfConsecutiveEmployeesInteger")
    @ApiOperation("【管理员】设置连续员工数量")
    public R numberOfConsecutiveEmployeesInteger(String value){
        String key = ApplicationConfigConstants.numberOfConsecutiveEmployeesInteger;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/defaultRecommendationCompanyInteger")
    @ApiOperation("【管理员】设置默认推荐公司数量")
    public R defaultRecommendationCompanyInteger(String value){
        String key = ApplicationConfigConstants.defaultRecommendationCompanyInteger;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/defaultRecommendationEmployeesInteger")
    @ApiOperation("【管理员】设置默认推荐员工数量")
    public R defaultRecommendationEmployeesInteger(String value){
        String key = ApplicationConfigConstants.defaultRecommendationEmployeesInteger;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/orderRetentionTime")
    @ApiOperation("【管理员】设置订单保留时间")
    public R orderRetentionTime(String value){
        String key = ApplicationConfigConstants.orderRetentionTime;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/automaticEvaluationTime")
    @ApiOperation("【管理员】设置订单自动好评时间(分钟)")
    public R automaticEvaluationTime(String value){
        String key = ApplicationConfigConstants.automaticEvaluationTime;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/bonus")
    @ApiOperation("【管理员】设置佣金金额（台币）")
    public R bonus(String value){
        String key = ApplicationConfigConstants.bonus;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/platformFeeCus")
    @ApiOperation("【管理员】设置平台费客戶(台币)")
    public R platformFeeCus(String value){
        String key = ApplicationConfigConstants.platformFeeCus;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/platformFeeEmp")
    @ApiOperation("【管理员】设置平台费員工(台币)")
    public R platformFeeEmp(String value){
        String key = ApplicationConfigConstants.platformFeeEmp;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @GetMapping("/platButton")
    @ApiOperation("【管理员】设置发布需求按钮")
    public R platButton(String value){
        String key = ApplicationConfigConstants.platButton;
        sysConfigService.config(key, value);
        return R.ok("设置成功");
    }

    @GetMapping("/getPlatButton")
    @ApiOperation("【管理员】获取发布需求")
    public R getPlatButton(){
        QueryWrapper qw = new QueryWrapper();
        qw.eq("config_key", "platButton");
        SysConfig one = sysConfigService.getOne(qw);
        return R.ok(one);
    }


    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping("/weight")
    @ApiOperation("【管理员】设置搜索权重")
    public R weight(@RequestBody WeightDTO dto){
        sysConfigService.config(ApplicationConfigConstants.distanceScoreDouble, dto.getDistanceScoreDouble());
        sysConfigService.config(ApplicationConfigConstants.areaScopeDouble, dto.getAreaScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.priceScopeDouble, dto.getPriceScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.attendanceScopeDouble, dto.getAttendanceScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.evaluateScopeDouble, dto.getEvaluateScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.extensionScopeDouble, dto.getExtensionScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.extensionCompanyScopeDouble, dto.getExtensionCompanyScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.numberOfOrdersReceivedScopeDouble, dto.getNumberOfOrdersReceivedScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.timeScopeDouble, dto.getTimeScopeDouble());
        sysConfigService.config(ApplicationConfigConstants.workScopeDouble, dto.getWorkScopeDouble());
        return R.ok("设置成功");
    }

}
