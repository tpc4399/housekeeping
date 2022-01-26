package com.housekeeping.admin.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRefundDTO {

    private String number;                    //订单编号
    private BigDecimal requirePrice;        //退款金额
    private String reason;                  //退款原因
    private String voucher;                 //凭证
    private String collectionAccount;       //收款账号
    private String bank;                    //银行
}
