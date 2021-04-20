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

    private Integer employeesId;                //订单甲方 保洁员 (一个)
    private String name1;                       //甲方姓名
    private String phPrefix1;                   //甲方手机号前缀
    private String phone1;                      //甲方手机号

    private Integer customerId;                 //订单乙方 客户 (一个)
    private String name2;                       //乙方姓名
    private String phPrefix2;                   //乙方手机号前缀
    private String phone2;                      //乙方手机号

    private String jobIds;                      //工作内容
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
     * 2 --> 未付款        待付款状态  To be paid
     * 5 --> 已付款        进行状态    have in hand
     * 8 --> 已做完工作     待确认状态  To be confirmed
     * 15 -->             待评价状态  To be evaluated
     * 20 --> 已评价       已完成状态  Completed
     */
    private Integer orderState;                 //订单状态

    private LocalDateTime payDateTime;          //付款时间
    private LocalDateTime completionDateTime;   //完成时间
    private LocalDateTime fixDateTime;          //确认时间
    private LocalDateTime evaluationDateTime;   //评价时间

}
