package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.SysAddressArea;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/3/8 16:40
 */
public interface ISysAddressAreaService extends IService<SysAddressArea> {

    R getAllByCityId(Integer cityId);

    /* 判斷地址在不在這些區 */
    Boolean matchingArea(String address, String areaIds);

}
