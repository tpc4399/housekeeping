package com.housekeeping.admin.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单的完整所有所有信息
 * @Author su
 * @Date 2021/4/15 9:37
 */
@Data
public class OrderDetailsPOJO {

    private Long number;                        //订单编号

    private List<EmployeesDetailsPOJO> emp;     //订单甲方 保洁员 (一个或多个)

    private Integer customerId;                 //订单乙方 客户 (一个)
    private String name;                        //客户姓名
    private String phone;                       //客户手机号
    private String address;                     //服务地址
    private Float lng;                          //经度
    private Float lat;                          //纬度

    private List<WorkDetailsPOJO> workDetails;  //订单安排详情 (工作内容、时间安排)
    private BigDecimal priceBeforeDiscount;     //优惠前的价格(台币元)
    private BigDecimal priceAfterDiscount;      //优惠后的价格(台币元)
    private List<Integer> discounts;            //参与到的优惠
    private String payType;                     //支付方式
    private String remarks;                     //备注
    private LocalDateTime startDateTime;        //订单生成时间
    private LocalDateTime updateDateTime;       //订单最后修改时间
    private LocalDateTime payDeadline;          //订单付款截止时间
    /**
     * 2 --> 未付款        待付款状态
     * 5 --> 已付款        进行状态
     * 8 --> 已做完工作     待确认状态
     * 15 -->             待评价状态
     * 20 --> 已评价       已完成状态
     */
    private Integer orderState;                 //订单状态

    private LocalDateTime payDateTime;          //付款时间
    private LocalDateTime completionDateTime;   //完成时间
    private LocalDateTime fixDateTime;          //确认时间
    private LocalDateTime evaluationDateTime;   //评价时间




}
