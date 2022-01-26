package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("token_order")
public class TokenOrder {

    //主键id
    @TableId(type= IdType.AUTO)
    private Integer id;

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

    public TokenOrder(TokenOrderParent odp) {
        this.number = odp.getNumber();
        ConsumptionItems = odp.getConsumptionItems();
        this.companyId = odp.getCompanyId();
        this.tokens = odp.getTokens();
        this.price = odp.getPrice();
        this.orderState = odp.getOrderState();
        this.payDateTime = odp.getPayDateTime();
        this.payType = odp.getPayType();
        this.payDeadline = odp.getPayDeadline();
        this.h = odp.getH();
    }

    public TokenOrder(Integer id, String number, String consumptionItems, String companyId, Integer tokens, Integer price, Integer orderState, LocalDateTime payDateTime, String payType, LocalDateTime payDeadline, Integer h) {
        this.id = id;
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
