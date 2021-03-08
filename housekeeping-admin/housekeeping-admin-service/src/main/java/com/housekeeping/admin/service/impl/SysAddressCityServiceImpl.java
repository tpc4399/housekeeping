package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysAddressCity;
import com.housekeeping.admin.mapper.SysAddressCityMapper;
import com.housekeeping.admin.service.ISysAddressCityService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/3/8 16:41
 */
@Service("sysAddressCityService")
public class SysAddressCityServiceImpl extends ServiceImpl<SysAddressCityMapper, SysAddressCity> implements ISysAddressCityService {
    @Override
    public R getAll() {
        return R.ok(this.list(), "獲取成功");
    }
}
