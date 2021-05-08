package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysConfig;
import com.housekeeping.admin.mapper.SysConfigMapper;
import com.housekeeping.admin.service.ISysConfigService;
import com.housekeeping.common.utils.ApplicationConfigConstants;
import com.housekeeping.common.utils.CommonUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/2/23 10:48
 */
@Service("sysConfigService")
public class SysConfigServiceImpl
        extends ServiceImpl<SysConfigMapper, SysConfig>
        implements ISysConfigService {

    @Override
    public Map<String, String> getScopeConfig(Integer priorityType) {
        QueryWrapper qw = new QueryWrapper();
        qw.ge("id", 8);
        qw.le("id", 16);
        List<SysConfig> res = this.list(qw);
        Map<String, String> map = new HashMap<>();
        res.forEach(x -> {
            map.put(x.getConfigKey(), x.getConfigValue());
        });
        if (CommonUtils.isNotEmpty(priorityType)){
            switch (priorityType){
                case 1: map.put(
                        ApplicationConfigConstants.distanceScoreDouble,
                        String.valueOf(new Double(map.get(ApplicationConfigConstants.distanceScoreDouble))+new Double(500.0))
                );
                    break;
                case 2: map.put(
                        ApplicationConfigConstants.areaScopeDouble,
                        String.valueOf(new Double(map.get(ApplicationConfigConstants.areaScopeDouble))+new Double(500.0))

                );
                    break;
                case 3: map.put(
                        ApplicationConfigConstants.priceScopeDouble,
                        String.valueOf(new Double(map.get(ApplicationConfigConstants.priceScopeDouble))+new Double(500.0))

                );
                    break;
                case 4: map.put(
                        ApplicationConfigConstants.attendanceScopeDouble,
                        String.valueOf(new Double(map.get(ApplicationConfigConstants.attendanceScopeDouble))+new Double(500.0))
                );
                    break;
                case 5: map.put(
                        ApplicationConfigConstants.evaluateScopeDouble,
                        String.valueOf(new Double(map.get(ApplicationConfigConstants.evaluateScopeDouble))+new Double(500.0))

                        );
                    break;
                case 6: map.put(
                        ApplicationConfigConstants.extensionScopeDouble,
                        String.valueOf(new Double(map.get(ApplicationConfigConstants.extensionScopeDouble))+new Double(500.0))
                );
                    break;
                case 7: map.put(
                        ApplicationConfigConstants.numberOfOrdersReceivedScopeDouble,
                        String.valueOf(new Double(map.get(ApplicationConfigConstants.numberOfOrdersReceivedScopeDouble))+new Double(500.0))
                );
                    break;
                default: ;
            }
        }
        return map;
    }

    @Override
    public Map<String, Integer> getNumber() {
        QueryWrapper qw = new QueryWrapper();
        qw.ge("id", 17);
        qw.le("id", 18);
        List<SysConfig> res = this.list(qw);
        Map<String, Integer> map = new HashMap<>();
        res.forEach(x -> {
            map.put(x.getConfigKey(), Integer.valueOf(x.getConfigValue()));
        });
        return map;
    }

    @Override
    public Map<String, Integer> getDefaultRecommendationInteger() {
        QueryWrapper qw = new QueryWrapper();
        qw.ge("id", 19);
        qw.le("id", 20);
        List<SysConfig> res = this.list(qw);
        Map<String, Integer> map = new HashMap<>();
        res.forEach(x -> {
            map.put(x.getConfigKey(), Integer.valueOf(x.getConfigValue()));
        });
        return map;
    }
}
