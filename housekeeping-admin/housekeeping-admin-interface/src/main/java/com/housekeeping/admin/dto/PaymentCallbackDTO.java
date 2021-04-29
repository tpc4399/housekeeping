package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/4/26 10:24
 */
@Data
public class PaymentCallbackDTO {

    private String classIf;     /* 付費方式
                                    A：刷卡
                                    B：虛擬帳號
                                    C：超商代收
                                    E：7-11ibon
                                    F：FamiPort
                                    I：i-Money
                                    L：LifeET
                                    O：黑貓貨到收現
                                    P：黑貓宅配
                                    Q：黑貓逆物流
                                    T：C2C取貨付款
                                    U：C2C純取貨
                                    V：B2C取貨付款
                                    W：B2C純取貨
                                    R：C2B客付
                                    S：C2B場附 */
    private String classIfSub;  /* 交付運送之超商或物流公司
                                    7NET：統一超商
                                    FAMI：全家(測試中)
                                    TCAT：黑貓 */
    private String odSob;       /* 消費項目(使用自訂繳款單方式二時無資料) */
    private String dataId;      /* 訂單號碼(使用自訂繳款單方式二時資料即為銷帳代碼) */
    private String processDate; /* 交易日期 ex:2010/10/11 */
    private String processTime; /* 交易時間  ex:上午 12:03:05 */
    private String responseId;  /* 1=授權成功   0=授權失敗 (刷卡作業才有此資料) */
    private String authCode;    /* 授權碼 (刷卡作業及 7-11ibon 才有此資料) */
    private String lastPan;     /* 卡號後 4 碼(刷卡作業才有此資料) */
    private String paymentNo;   /* 交易號碼(付款於ATM虛擬帳號、ibon代碼、famiport代碼) */
    private String purchase;    /* 交易金額 */
    private String amount;      /* 成交金額( 實際成交金額，刷卡失敗時為0 ) */
    private String errReason;   /* 交易失敗原因 (刷卡作業才有此資料) */
    private String name;        /* 消費者姓名(使用自訂繳款單方式二時無資料) */
    private String tel;         /* 消費者聯絡電話(使用自訂繳款單方式二時無資料) */
    private String phone;       /* 消費者行動電話(使用自訂繳款單方式二時無資料) */
    private String address;     /* 送貨地址(使用自訂繳款單方式二時無資料) */
    private String email;       /* 消費者電子信箱(使用自訂繳款單方式二時無資料) */
    private String invoiceNum;  /* 消費者統一編號(使用自訂繳款單方式二時無資料) */
    private String remark;      /* 備註(使用自訂繳款單方式二時無資料) */
    private String smileId;     /* SmilePay追蹤碼 */
    private String foreign;     /* Y=國外卡,N=國內卡,U=銀聯卡 (一般商家刷卡方案才有資料) */
    private String verifyNumber;/* 交易認證電話 */
    private String midSmilePay; /* SmilePay 驗證碼(驗證是否由SmilePay送出的資料) */

    @Override
    public String toString() {
        return "PaymentCallbackDTO{" +
                "classIf='" + classIf + '\'' +
                ", classIfSub='" + classIfSub + '\'' +
                ", odSob='" + odSob + '\'' +
                ", dataId='" + dataId + '\'' +
                ", processDate='" + processDate + '\'' +
                ", processTime='" + processTime + '\'' +
                ", responseId='" + responseId + '\'' +
                ", authCode='" + authCode + '\'' +
                ", lastPan='" + lastPan + '\'' +
                ", paymentNo='" + paymentNo + '\'' +
                ", purchase='" + purchase + '\'' +
                ", amount='" + amount + '\'' +
                ", errReason='" + errReason + '\'' +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", invoiceNum='" + invoiceNum + '\'' +
                ", remark='" + remark + '\'' +
                ", smileId='" + smileId + '\'' +
                ", foreign='" + foreign + '\'' +
                ", verifyNumber='" + verifyNumber + '\'' +
                ", midSmilePay='" + midSmilePay + '\'' +
                '}';
    }
}
