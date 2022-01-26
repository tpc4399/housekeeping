package com.housekeeping.admin.service;

import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.entity.OrderPhotos;
import com.housekeeping.common.utils.R;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/28 16:09
 */
public interface IOrderPhotosService {

    /* 【保洁员】查询订单的工作重点，以及回传信息 */
    R getByOrderNumber(String orderNumber);

    /* 批量插入 */
    void saveBatch(List<OrderPhotos> ops);

    /* 根据订单编号查询 */
    List<OrderPhotos> listByNumber(String number);

}
