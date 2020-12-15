package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysForeignCurrency;
import com.housekeeping.admin.mapper.SysForeignCurrencyMapper;
import com.housekeeping.admin.service.ISysForeignCurrencyService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2020/12/15 16:37
 */
@Service("sysForeignCurrencyService")
public class SysForeignCurrencyServiceImpl
        extends ServiceImpl<SysForeignCurrencyMapper, SysForeignCurrency>
        implements ISysForeignCurrencyService {
}
