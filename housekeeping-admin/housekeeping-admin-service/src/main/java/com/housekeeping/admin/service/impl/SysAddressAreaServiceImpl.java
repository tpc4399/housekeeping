package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysAddressArea;
import com.housekeeping.admin.mapper.SysAddressAreaMapper;
import com.housekeeping.admin.service.ISysAddressAreaService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author su
 * @Date 2021/3/8 16:40
 */
@Service("sysAddressAreaService")
public class SysAddressAreaServiceImpl extends ServiceImpl<SysAddressAreaMapper, SysAddressArea> implements ISysAddressAreaService {
    @Override
    public R getAllByCityId(Integer cityId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("parent_id", cityId);
        List<SysAddressArea> res = this.list(qw);
        return R.ok(res, "查詢成功");
    }
}
