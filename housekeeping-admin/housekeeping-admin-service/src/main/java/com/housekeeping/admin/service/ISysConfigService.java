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
    Map<String, Integer> getNumber();

}
