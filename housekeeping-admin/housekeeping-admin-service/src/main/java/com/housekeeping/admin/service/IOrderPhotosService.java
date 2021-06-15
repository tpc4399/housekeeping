package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.entity.OrderPhotos;
import com.housekeeping.common.utils.R;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/28 16:09
 */
public interface IOrderPhotosService extends IService<OrderPhotos> {

    /* 查询订单的保洁员是否已进行工作重点的回传 -1：程序问题 0:未回传 1：部分未回传 2：已回传 3:无需回传 */
    Integer isCallback(String number);

    /* 【保洁员】对订单进行工作重点回传 */
    R keyWorkReturn(List<KeyWorkReturnDTO> dto);

    /* 【保洁员】查询订单的工作重点，以及回传信息 */
    R getByOrderNumber(String orderNumber);

}
