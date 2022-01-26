package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.housekeeping.admin.dto.CardPayCallbackParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @create 2021/6/2 9:24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("card_pay_callback")
@AllArgsConstructor
@NoArgsConstructor
public class CardPayCallback extends Model<CardPayCallback> {

    private String merchantID; //特店編號
    private String merchantTradeNo; //特店交易編號
    private String storeID; //特店旗下店舖代號
    private String rtnCode; //交易狀態
    private String rtnMsg; //交易訊息
    private String tradeNo; //綠界的交易編號
    private String tradeAmt; //交易金額
    private String paymentDate; //付款時間
    private String paymentType; //特店選擇的付款方式
    private String paymentTypeChargeFee; //通路費
    private String tradeDate; //訂單成立時間
    private String simulatePaid; //是否為模擬付款
    private String customField1; //自訂名稱欄位 1
    private String customField2; //自訂名稱欄位 2
    private String customField3; //自訂名稱欄位 3
    private String customField4; //自訂名稱欄位 4
    private String checkMacValue; //檢查碼

    public CardPayCallback(CardPayCallbackParams params) {
        merchantID = params.getMerchantID();
        merchantTradeNo = params.getMerchantTradeNo();
        storeID = params.getStoreID();
        rtnCode = params.getRtnCode();
        rtnMsg = params.getRtnMsg();
        tradeNo = params.getTradeNo();
        tradeAmt = params.getTradeAmt();
        paymentDate = params.getPaymentDate();
        paymentType = params.getPaymentType();
        paymentTypeChargeFee = params.getPaymentTypeChargeFee();
        tradeDate = params.getTradeDate();
        simulatePaid = params.getSimulatePaid();
        customField1 = params.getCustomField1();
        customField2 = params.getCustomField2();
        customField3 = params.getCustomField3();
        customField4 = params.getCustomField4();
        checkMacValue = params.getCheckMacValue();
    }
}
