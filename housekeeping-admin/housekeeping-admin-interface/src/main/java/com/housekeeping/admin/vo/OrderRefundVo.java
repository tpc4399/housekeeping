package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRefundVo {
    private Integer id;                     //主键id
    private Integer customerId;             //客户id
    private Long number;                    //订单编号
    private BigDecimal requirePrice;        //退款金额
    private String reason;                  //退款原因
    private String voucher;                 //凭证
    private String collectionAccount;       //收款账号
    private Boolean companyAgree;           //公司是否同意
    private BigDecimal companyPrice;        //公司退款金额
    private String companyReason;           //公司原因
    private String companyVoucher;          //公司上传凭证
    private BigDecimal financePrice;        //财务退款金额
    private String financeVoucher;          //财务转账拍照
    private Integer status;                 //退款状态（默认0退款中）1
    private OrderDetailsPOJO orderDetailsPOJO;
    private CustomerDetails customerDetails;


    public OrderRefundVo(Integer id, Integer customerId, Long number, BigDecimal requirePrice, String reason, String voucher, String collectionAccount, Boolean companyAgree, BigDecimal companyPrice, String companyReason, String companyVoucher, BigDecimal financePrice, String financeVoucher, Integer status, OrderDetailsPOJO orderDetailsPOJO, CustomerDetails customerDetails) {
        this.id = id;
        this.customerId = customerId;
        this.number = number;
        this.requirePrice = requirePrice;
        this.reason = reason;
        this.voucher = voucher;
        this.collectionAccount = collectionAccount;
        this.companyAgree = companyAgree;
        this.companyPrice = companyPrice;
        this.companyReason = companyReason;
        this.companyVoucher = companyVoucher;
        this.financePrice = financePrice;
        this.financeVoucher = financeVoucher;
        this.status = status;
        this.orderDetailsPOJO = orderDetailsPOJO;
        this.customerDetails = customerDetails;
    }

    public OrderRefundVo() {
    }
}
