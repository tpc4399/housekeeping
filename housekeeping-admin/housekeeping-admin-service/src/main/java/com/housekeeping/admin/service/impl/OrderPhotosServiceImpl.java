package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.entity.OrderPhotos;
import com.housekeeping.admin.mapper.OrderPhotosMapper;
import com.housekeeping.admin.service.IOrderPhotosService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/28 16:12
 */
@Service("orderPhotosService")
public class OrderPhotosServiceImpl implements IOrderPhotosService {

    @Resource
    private OrderPhotosMapper orderPhotosMapper;

    @Override
    public R getByOrderNumber(String orderNumber) {
        List<OrderPhotos> orderPhotos = orderPhotosMapper.getByOrderNumber(orderNumber);
        return R.ok(orderPhotos, "查詢成功");
    }

    @Override
    public void saveBatch(List<OrderPhotos> ops) {
        orderPhotosMapper.saveBatch(ops);
    }

    @Override
    public List<OrderPhotos> listByNumber(String number) {
        return orderPhotosMapper.listByNumber(number);
    }
}
