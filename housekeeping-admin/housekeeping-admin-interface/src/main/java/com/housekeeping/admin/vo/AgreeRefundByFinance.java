package com.housekeeping.admin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgreeRefundByFinance {

    private Integer id;
    private BigDecimal companyPrice;        //財務退款金额
    private String companyVoucher;          //財務上传圖片
}
