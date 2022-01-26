package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/4/29 15:40
 */
@Data
public class PaymentCallbackParams {

    private String Classif;         /* 付费方式 */
    private String Classif_sub;     /* 交付運送之超商或物流公司
                                        7NET：統一超商
                                        FAMI：全家(測試中)
                                        TCAT：黑貓 */
    private String Od_sob;          /* 消費項目(使用自訂繳款單方式二時無資料) */
    private String Data_id;         /* 訂單號碼(使用自訂繳款單方式二時資料即為銷帳代碼) */
    private String Process_date;    /* 交易日期 ex:2010/10/11 */
    private String Process_time;    /* 交易時間  ex:上午 12:03:05 */
    private String Response_id;     /* 1=授權成功   0=授權失敗 (刷卡作業才有此資料) */
    private String Auth_code;       /* 授權碼 (刷卡作業及 7-11ibon 才有此資料) */
    private String LastPan;         /* 卡號後 4 碼(刷卡作業才有此資料) */
    private String Payment_no;      /* 交易號碼(付款於ATM虛擬帳號、ibon代碼、famiport代碼) */
    private String Purchamt;        /* 交易金額 */
    private String Amount;          /* 成交金額( 實際成交金額，刷卡失敗時為0 ) */
    private String Errdesc;         /* 交易失敗原因 (刷卡作業才有此資料) */
    private String Pur_name;        /* 消費者姓名(使用自訂繳款單方式二時無資料) */
    private String Tel_number;      /* 消費者聯絡電話(使用自訂繳款單方式二時無資料) */
    private String Mobile_number;   /* 消費者行動電話(使用自訂繳款單方式二時無資料) */
    private String Address;         /* 送貨地址(使用自訂繳款單方式二時無資料) */
    private String email;           /* 消費者電子信箱(使用自訂繳款單方式二時無資料) */
    private String Invoice_num;     /* 消費者統一編號(使用自訂繳款單方式二時無資料) */
    private String Remark;          /* 備註(使用自訂繳款單方式二時無資料) */
    private String Smseid;          /* SmilePay追蹤碼 */
    private String Foreign;         /* Y=國外卡,N=國內卡,U=銀聯卡 (一般商家刷卡方案才有資料) */
    private String Verify_number;   /* 交易認證電話 */
    private String Mid_smilepay;    /* SmilePay 驗證碼(驗證是否由SmilePay送出的資料) */

    @Override
    public String toString() {
        return "PaymentCallbackParams{" +
                "Classif='" + Classif + '\'' +
                ", Classif_sub='" + Classif_sub + '\'' +
                ", Od_sob='" + Od_sob + '\'' +
                ", Data_id='" + Data_id + '\'' +
                ", Process_date='" + Process_date + '\'' +
                ", Process_time='" + Process_time + '\'' +
                ", Response_id='" + Response_id + '\'' +
                ", Auth_code='" + Auth_code + '\'' +
                ", LastPan='" + LastPan + '\'' +
                ", Payment_no='" + Payment_no + '\'' +
                ", Purchamt='" + Purchamt + '\'' +
                ", Amount='" + Amount + '\'' +
                ", Errdesc='" + Errdesc + '\'' +
                ", Pur_name='" + Pur_name + '\'' +
                ", Tel_number='" + Tel_number + '\'' +
                ", Mobile_number='" + Mobile_number + '\'' +
                ", Address='" + Address + '\'' +
                ", email='" + email + '\'' +
                ", Invoice_num='" + Invoice_num + '\'' +
                ", Remark='" + Remark + '\'' +
                ", Smseid='" + Smseid + '\'' +
                ", Foreign='" + Foreign + '\'' +
                ", Verify_number='" + Verify_number + '\'' +
                ", Mid_smilepay='" + Mid_smilepay + '\'' +
                '}';
    }
}
