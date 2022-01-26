package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.WeightDTO;
import com.housekeeping.admin.entity.SysConfig;
import com.housekeeping.admin.mapper.SysConfigMapper;
import com.housekeeping.admin.service.ISysConfigService;
import com.housekeeping.common.utils.ApplicationConfigConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public Map<String, String> getQueryWeight(Integer priorityType) {
        List<String> keys = new ArrayList<>();
        keys.add(ApplicationConfigConstants.timeScopeDouble);
        keys.add(ApplicationConfigConstants.workScopeDouble);
        keys.add(ApplicationConfigConstants.priceScopeDouble);
        keys.add(ApplicationConfigConstants.distanceScoreDouble);
        keys.add(ApplicationConfigConstants.extensionScopeDouble);
        keys.add(ApplicationConfigConstants.evaluateScopeDouble);
        keys.add(ApplicationConfigConstants.areaScopeDouble);

        QueryWrapper qw = new QueryWrapper();
        qw.in("config_key", keys);
        List<SysConfig> res = this.list(qw);
        Map<String, String> map = new HashMap<>();
        res.forEach(x -> {
            map.put(x.getConfigKey(), x.getConfigValue());
        });
        /* 排序类型
                                         0 | null 默认排序
                                         1    价格合适排序
                                         2    距离排序
                                         3    评价排序
                                         4    钟点工作内容排序
                                    */
        if (priorityType.equals(1)){
            //价格->工作内容->可工作區域->距离->评价
            map.put(ApplicationConfigConstants.priceScopeDouble, "100000");  //价格
            map.put(ApplicationConfigConstants.workScopeDouble, "10000");  //工作内容
            map.put(ApplicationConfigConstants.areaScopeDouble, "5000");  //可工作區域
            map.put(ApplicationConfigConstants.distanceScoreDouble, "1000"); //距离
            map.put(ApplicationConfigConstants.evaluateScopeDouble, "100"); //评价
        }
        if (priorityType.equals(2)){
            //距离->工作内容->可工作區域->价格->评价
            map.put(ApplicationConfigConstants.distanceScoreDouble, "100000"); //距离
            map.put(ApplicationConfigConstants.workScopeDouble, "10000");  //工作内容
            map.put(ApplicationConfigConstants.areaScopeDouble, "5000");  //可工作區域
            map.put(ApplicationConfigConstants.priceScopeDouble, "1000");  //价格
            map.put(ApplicationConfigConstants.evaluateScopeDouble, "100"); //评价
        }
        if (priorityType.equals(3)){
            //评价->工作内容->可工作區域->距离->价格
            map.put(ApplicationConfigConstants.evaluateScopeDouble, "100000"); //评价
            map.put(ApplicationConfigConstants.workScopeDouble, "10000");  //工作内容
            map.put(ApplicationConfigConstants.areaScopeDouble, "5000");  //可工作區域
            map.put(ApplicationConfigConstants.distanceScoreDouble, "1000"); //距离
            map.put(ApplicationConfigConstants.priceScopeDouble, "100");  //价格
        }
        if (priorityType.equals(4)){
            //工作内容->可工作區域->距离->价格->评价
            map.put(ApplicationConfigConstants.workScopeDouble, "100000");  //工作内容
            map.put(ApplicationConfigConstants.areaScopeDouble, "5000");  //可工作區域
            map.put(ApplicationConfigConstants.distanceScoreDouble, "10000"); //距离
            map.put(ApplicationConfigConstants.priceScopeDouble, "1000");  //价格
            map.put(ApplicationConfigConstants.evaluateScopeDouble, "100"); //评价
        }
        return map;
    }

    @Override
    public R config(String key, String value) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("config_key", key);
        SysConfig config = this.getOne(qw);
        if (CommonUtils.isEmpty(config)) {
            this.save(new SysConfig(null, key, value, config.getDescription()));
        } else {
            if (value.equals(config.getConfigValue())) {
                //还是这个值，不用管
            } else {
                this.updateById(new SysConfig(config.getId(), key, value, config.getDescription()));
            }
        }
        return R.ok("设置成功");
    }

    @Override
    public String getAutomaticEvaluationTime() {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("config_key", ApplicationConfigConstants.automaticEvaluationTime);
        SysConfig config = this.getOne(qw);
        return config.getConfigValue();
    }

}
