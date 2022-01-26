package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.SysAddressCity;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/3/8 16:40
 */
public interface ISysAddressCityService extends IService<SysAddressCity> {

    R getAll();

}
