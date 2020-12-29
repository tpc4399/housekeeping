package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysOrderContractorDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.entity.SysOrderContractor;
import com.housekeeping.admin.mapper.SysOrderContractorMapper;
import com.housekeeping.admin.service.ISysOrderContractorService;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service("sysOrderContractorService")
public class SysOrderContractorServiceImpl extends ServiceImpl<SysOrderContractorMapper, SysOrderContractor> implements ISysOrderContractorService {

    @Resource
    private ISysOrderService sysOrderService;

    @Override
    public R releaseOrderContractor(SysOrderContractorDTO sysOrderPlanDTO) {
        SysOrder sysOrder = new SysOrder();
        sysOrder.setType(sysOrderPlanDTO.getTemp());
        if (sysOrder.getType()){
            sysOrder.setCompanyId(sysOrderPlanDTO.getCompanyId());
        }
        sysOrder.setAddressId(sysOrderPlanDTO.getAddressId());
        sysOrder.setJobContendIds(sysOrderPlanDTO.getJobContendIds());
        AtomicReference<Integer> maxId = new AtomicReference<>(0);
        synchronized (this){
            sysOrderService.releaseOrder(sysOrder);
            maxId.set(((SysOrder) CommonUtils.getMaxId("sys_order", sysOrderService)).getId());
        }
        SysOrderContractor sysOrderContractor = new SysOrderContractor();
        sysOrderContractor.setForwardTime(sysOrderPlanDTO.getForwardTime());
        sysOrderContractor.setOrderId(maxId.get());
        sysOrderContractor.setStartTime(sysOrderPlanDTO.getStartTime());
        Boolean save = this.save(sysOrderContractor);
        return R.ok(save);
    }
}
