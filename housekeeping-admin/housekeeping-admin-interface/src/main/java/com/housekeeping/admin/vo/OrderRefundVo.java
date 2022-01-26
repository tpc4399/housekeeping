package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderRefundVo {
    private Integer id;                     //主键id
    private Integer customerId;             //客户id
    private String number;                    //订单编号
    private BigDecimal requirePrice;        //退款金额
    private String reason;                  //退款原因
    private String voucher;                 //凭证
    private String collectionAccount;       //收款账号
    private String bank;                    //银行
    private Boolean companyAgree;           //公司是否同意
    private BigDecimal companyPrice;        //公司退款金额
    private String companyReason;           //公司原因
    private String companyVoucher;          //公司上传凭证
    private BigDecimal financePrice;        //财务退款金额
    private String financeVoucher;          //财务转账拍照
    private Integer status;                 //退款状态（默认0退款中）1已拒絕 2已完成
    private LocalDateTime createTime;       //退款申請時間
    private LocalDateTime companyTime;      //公司操作時間
    private LocalDateTime financeTime;      //財務操作時間
    private OrderDetailsPOJO orderDetailsPOJO;
    private CustomerDetails customerDetails;


    public OrderRefundVo(Integer id, Integer customerId, String number, BigDecimal requirePrice, String reason, String voucher, String collectionAccount, String bank, Boolean companyAgree, BigDecimal companyPrice, String companyReason, String companyVoucher, BigDecimal financePrice, String financeVoucher, Integer status, LocalDateTime createTime, LocalDateTime companyTime, LocalDateTime financeTime, OrderDetailsPOJO orderDetailsPOJO, CustomerDetails customerDetails) {
        this.id = id;
        this.customerId = customerId;
        this.number = number;
        this.requirePrice = requirePrice;
        this.reason = reason;
        this.voucher = voucher;
        this.collectionAccount = collectionAccount;
        this.bank = bank;
        this.companyAgree = companyAgree;
        this.companyPrice = companyPrice;
        this.companyReason = companyReason;
        this.companyVoucher = companyVoucher;
        this.financePrice = financePrice;
        this.financeVoucher = financeVoucher;
        this.status = status;
        this.createTime = createTime;
        this.companyTime = companyTime;
        this.financeTime = financeTime;
        this.orderDetailsPOJO = orderDetailsPOJO;
        this.customerDetails = customerDetails;
    }

    public OrderRefundVo() {
    }
}
