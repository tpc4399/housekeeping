package com.housekeeping.order.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生成的订单详情
 * @Author su
 * @Date 2021/4/15 9:37
 */
public class OrderDetailsPOJO {

    private Long number;          //订单编号
    private Integer employeesId;  //订单服务方 保洁员
    private Integer customerId;     //订单来自方 客户
    private WorkDetailsPOJO workDetails;    //订单安排详情 (工作内容、时间安排)
    private BigDecimal priceBeforeDiscount;     //优惠前的价格(台币元)
    private BigDecimal priceAfterDiscount;      //优惠后的价格(台币元)
    private List<Integer> discounts;        //参与到的优惠
    private String payType;     //支付方式
    private String remarks;     //备注
    private LocalDateTime startDateTime;    //订单生成时间
    private LocalDateTime updateDateTime;   //订单最后修改时间
    private LocalDateTime payDeadline;      //订单付款截止时间
    /**
     * 2 --> 未付款 待付款
     * 5 --> 已付款 进行中
     * 8 --> 已完成 待确认
     * 15 --> 待评价
     */
    private Integer orderState;             //订单状态




}
