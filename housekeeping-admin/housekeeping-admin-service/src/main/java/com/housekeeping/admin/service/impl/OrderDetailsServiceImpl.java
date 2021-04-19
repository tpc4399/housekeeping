package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.mapper.OrderDetailsMapper;
import com.housekeeping.admin.service.IOrderDetailsService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/4/19 9:46
 */
@Service("orderDetailsService")
public class OrderDetailsServiceImpl extends ServiceImpl<OrderDetailsMapper, OrderDetails> implements IOrderDetailsService {
}
