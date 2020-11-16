package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.entity.SysOrderPlan;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @create 2020/11/16 14:09
 */
public interface ISysOrderPlanService extends IService<SysOrderPlan> {

    R releaseOrderPlan(SysOrderPlanDTO sysOrderPlanDTO);

}
