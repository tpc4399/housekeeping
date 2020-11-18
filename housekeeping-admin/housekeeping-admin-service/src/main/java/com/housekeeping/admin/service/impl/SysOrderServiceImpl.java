package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.mapper.SysOrderMapper;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.common.utils.CommonUtils;
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

    @Override
    public IPage<SysOrder> page(IPage<SysOrder> page, SysOrder sysOrder) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(sysOrder.getNumber())){
            queryWrapper.like("number", sysOrder.getNumber());
        }
        if (CommonUtils.isNotEmpty(sysOrder.getCompanyId())){
            queryWrapper.eq("company_id", sysOrder.getCompanyId());
        }
        if (CommonUtils.isNotEmpty(sysOrder.getCustomerId())){
            queryWrapper.like("customer_id", sysOrder.getCustomerId());
        }
        if (CommonUtils.isNotEmpty(sysOrder.getAddressId())){
            queryWrapper.like("address_id", sysOrder.getAddressId());
        }
        if (CommonUtils.isNotEmpty(sysOrder.getType())){
            queryWrapper.like("type", sysOrder.getType());
        }
        if (CommonUtils.isNotEmpty(sysOrder.getCreateTime())){
            queryWrapper.like("create_time", sysOrder.getCreateTime());
        }
        if (CommonUtils.isNotEmpty(sysOrder.getTotalTime())){
            queryWrapper.like("total_time", sysOrder.getTotalTime());
        }
        return baseMapper.selectPage(page, queryWrapper);
    }
}
