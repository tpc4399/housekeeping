package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * 信用卡支付 回調接口接收參數
 * @Author su
 * @create 2021/6/1 16:54
 */
@Data
public class CardPayCallbackParams {

    private String MerchantID; //特店編號
    private String MerchantTradeNo; //特店交易編號
    private String StoreID; //特店旗下店舖代號
    private String RtnCode; //交易狀態
    private String RtnMsg; //交易訊息
    private String TradeNo; //綠界的交易編號
    private String TradeAmt; //交易金額
    private String PaymentDate; //付款時間
    private String PaymentType; //特店選擇的付款方式
    private String PaymentTypeChargeFee; //通路費
    private String TradeDate; //訂單成立時間
    private String SimulatePaid; //是否為模擬付款
    private String CustomField1; //自訂名稱欄位 1
    private String CustomField2; //自訂名稱欄位 2
    private String CustomField3; //自訂名稱欄位 3
    private String CustomField4; //自訂名稱欄位 4
    private String CheckMacValue; //檢查碼

}
