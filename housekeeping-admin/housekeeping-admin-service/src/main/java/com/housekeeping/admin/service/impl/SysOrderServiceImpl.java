package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.mapper.SysOrderMapper;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/16 14:47
 */
@Service("sysOrderService")
public class SysOrderServiceImpl extends ServiceImpl<SysOrderMapper, SysOrder> implements ISysOrderService {
    @Override
    public R releaseOrder(SysOrder sysOrder) {
        sysOrder.setCreateTime(LocalDateTime.now());
        sysOrder.setCustomerId(TokenUtils.getCurrentUserId());
        baseMapper.insert(sysOrder);
        return R.ok();
    }
}
