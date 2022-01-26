package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.PayToken;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.TokenOrder;
import com.housekeeping.common.utils.R;

public interface TokenOrderService extends IService<TokenOrder> {
    R payToken(PayToken dto);

    R pay(Long number, String payType);

    R inputSql(String number);

    String cardPayByToken(String number, String callBackUrl);
}
