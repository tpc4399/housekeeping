package com.housekeeping.admin.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgreeRefund {

    private Integer id;
    private BigDecimal companyPrice;        //公司退款金额
    private String companyReason;           //公司原因
    private String companyVoucher;          //公司上传凭证

}
