package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/16 14:46
 */
public interface ISysOrderService extends IService<SysOrder> {

    R releaseOrder(SysOrder sysOrder);

}
