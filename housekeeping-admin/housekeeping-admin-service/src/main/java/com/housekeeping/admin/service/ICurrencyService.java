package com.housekeeping.admin.service;

import com.housekeeping.common.utils.R;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/12 15:59
 */
public interface ICurrencyService {

    R exchangeRate(String fromCode, String toCode, BigDecimal money);

    BigDecimal exchangeRateToBigDecimal(String fromCode, String toCode, BigDecimal money);

}
