package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CardPayCallback;
import com.housekeeping.admin.mapper.CardPayCallbackMapper;
import com.housekeeping.admin.service.ICardPayCallbackService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2021/6/2 10:06
 */
@Service("cardPayCallbackService")
public class CardPayCallbackServiceImpl
        extends ServiceImpl<CardPayCallbackMapper, CardPayCallback>
        implements ICardPayCallbackService {
}
