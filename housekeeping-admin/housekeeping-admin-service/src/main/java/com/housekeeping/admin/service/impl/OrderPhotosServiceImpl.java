package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.OrderPhotos;
import com.housekeeping.admin.mapper.OrderPhotosMapper;
import com.housekeeping.admin.service.IOrderPhotosService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/4/28 16:12
 */
@Service("orderPhotosService")
public class OrderPhotosServiceImpl
        extends ServiceImpl<OrderPhotosMapper, OrderPhotos>
        implements IOrderPhotosService {
}
