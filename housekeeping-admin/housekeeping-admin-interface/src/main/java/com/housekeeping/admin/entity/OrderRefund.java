package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_refund")
public class OrderRefund extends Model<OrderRefund> {

    @TableId(type= IdType.AUTO)
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
}
