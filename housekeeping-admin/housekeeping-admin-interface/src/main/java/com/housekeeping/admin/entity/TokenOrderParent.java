package com.housekeeping.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class TokenOrderParent {

    //订单编号
    private String number;
    //消费项目
    private String ConsumptionItems;
    //公司id
    private String companyId;
    //代币数
    private Integer tokens;
    //价格
    private Integer price;
    //订单状态
    private Integer orderState;
    /*付款时间*/
    private LocalDateTime payDateTime;
    //支付方式
    private String payType;
    //订单付款截止时间
    private LocalDateTime payDeadline;
    //订单保留时長
    private Integer h;

    public TokenOrderParent() {
    }

    public TokenOrderParent(TokenOrder tokenOrder) {
        this.number = tokenOrder.getNumber();
        ConsumptionItems = tokenOrder.getConsumptionItems();
        this.companyId = tokenOrder.getCompanyId();
        this.tokens = tokenOrder.getTokens();
        this.price = tokenOrder.getPrice();
        this.orderState = tokenOrder.getOrderState();
        this.payDateTime = tokenOrder.getPayDateTime();
        this.payType = tokenOrder.getPayType();
        this.payDeadline = tokenOrder.getPayDeadline();
        this.h = tokenOrder.getH();
    }

    public TokenOrderParent(String number, String consumptionItems, String companyId, Integer tokens, Integer price, Integer orderState, LocalDateTime payDateTime, String payType, LocalDateTime payDeadline, Integer h) {
        this.number = number;
        ConsumptionItems = consumptionItems;
        this.companyId = companyId;
        this.tokens = tokens;
        this.price = price;
        this.orderState = orderState;
        this.payDateTime = payDateTime;
        this.payType = payType;
        this.payDeadline = payDeadline;
        this.h = h;
    }
}
