package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.SysConfig;
import com.housekeeping.common.utils.R;

import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/2/23 10:47
 */
public interface ISysConfigService extends IService<SysConfig> {

    Map<String, String> getScopeConfig(Integer priorityType);
    /* 获取主页搜索结果页面员工数量与公司数量比例 */
    Map<String, Integer> getNumber();
    /* 获取默认推荐数量 */
    Map<String, Integer> getDefaultRecommendationInteger();

}
