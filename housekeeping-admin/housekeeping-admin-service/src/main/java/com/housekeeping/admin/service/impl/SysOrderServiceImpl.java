package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysOrderDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.mapper.SysOrderMapper;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 *
 * 第一种写法（1）：
 *
 * 原符号       <        <=      >       >=       &        '        "
 * 替换符号    &lt;    &lt;=   &gt;    &gt;=   &amp;   &apos;  &quot;
 * 例如：sql如下：
 * create_date_time &gt;= #{startTime} and  create_date_time &lt;= #{endTime}
 *
 * 第二种写法（2）：
 * 大于等于
 * <![CDATA[ >= ]]>
 * 小于等于
 * <![CDATA[ <= ]]>
 * 例如：sql如下：
 * create_date_time <![CDATA[ >= ]]> #{startTime} and  create_date_time <![CDATA[ <= ]]> #{endTime}
 *
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
    public IPage<SysOrder> page(IPage<SysOrder> page, SysOrderDTO sysOrderDTO) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(sysOrderDTO.getNumber())){
            queryWrapper.like("number", sysOrderDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCompanyId())){
            queryWrapper.eq("company_id", sysOrderDTO.getCompanyId());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCustomerId())){
            queryWrapper.eq("customer_id", sysOrderDTO.getCustomerId());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getAddressId())){
            queryWrapper.eq("address_id", sysOrderDTO.getAddressId());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getType())){
            queryWrapper.eq("type", sysOrderDTO.getType());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCreateTimeStart())){
            queryWrapper.ge("create_time", sysOrderDTO.getCreateTimeStart());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCreateTimeEnd())){
            queryWrapper.le("create_time", sysOrderDTO.getCreateTimeEnd());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getTotalTimeMin())){
            queryWrapper.ge("total_time", sysOrderDTO.getTotalTimeMin());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getTotalTimeMax())){
            queryWrapper.le("total_time", sysOrderDTO.getTotalTimeMax());
        }
        return baseMapper.selectPage(page, queryWrapper);
    }
}
