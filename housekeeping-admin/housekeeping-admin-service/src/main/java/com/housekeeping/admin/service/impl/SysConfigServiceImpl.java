package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysConfig;
import com.housekeeping.admin.mapper.SysConfigMapper;
import com.housekeeping.admin.service.ISysConfigService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/2/23 10:48
 */
@Service("sysConfigService")
public class SysConfigServiceImpl
        extends ServiceImpl<SysConfigMapper, SysConfig>
        implements ISysConfigService {
}
