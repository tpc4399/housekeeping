package com.housekeeping.admin.pojo;

import com.housekeeping.admin.entity.OrderDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单的完整所有所有信息
 * @Author su
 * @Date 2021/4/15 9:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsPOJO {

    private String number;                      //订单编号
    private String consumptionItems;            //消费项目

    private Integer employeesId;                //订单甲方 保洁员 (一个)
    private String name1;                       //甲方姓名
    private String phPrefix1;                   //甲方手机号前缀
    private String phone1;                      //甲方手机号
    private Integer companyId;                  //甲方所属公司
    private String invoiceName;                 //公司发票抬头
    private String invoiceNumber;               //公司统一编号

    private Integer customerId;                 //订单乙方 客户 (一个)
    private String name2;                       //乙方姓名
    private String phPrefix2;                   //乙方手机号前缀
    private String phone2;                      //乙方手机号

    private String jobIds;                      //工作内容
    private String address;                     //服务地址
    private Float lng;                          //经度
    private Float lat;                          //纬度

    private List<WorkDetailsPOJO> workDetails;  //订单安排详情 (工作内容、时间安排)
    private Integer days;                       //天数
    private Float hOfDay;                       //每天的小时数
    private BigDecimal priceBeforeDiscount;     //优惠前的价格(台币元)
    private BigDecimal priceAfterDiscount;      //优惠后的价格(台币元)
    private String payType;                     //支付方式
    private List<OrderPhotoPOJO> photos;        //订单的照片一级评论
    private String remarks;                     //备注
    private LocalDateTime startDateTime;        //订单生成时间
    private LocalDateTime updateDateTime;       //订单最后修改时间
    private Integer h;                          //订单保留时间
    private LocalDateTime payDeadline;          //订单付款截止时间
    /**
     * 0 --> 订单作废中     订单不进行保留 Order void
     * 2 --> 未付款        待付款状态  To be paid
     * 3 --> 付款处理中     已付款但是还没收到付款的 Payment processing
     * 4 --> 已付款        待服务      To be served
     * 5 --> 已付款        进行状态    have in hand
     * 8 --> 已做完工作     待确认状态  To be confirmed
     * 15 -->             待评价状态  To be evaluated
     * 20 --> 已评价       已完成状态  Completed
     */
    private Integer orderState;                 //订单状态
    private Integer orderOrigin;                //订单来源 0钟点工 1包工

    private LocalDateTime payDateTime;          //付款时间
    private LocalDateTime completionDateTime;   //完成时间
    private LocalDateTime fixDateTime;          //确认时间
    private LocalDateTime evaluationDateTime;   //评价时间

    public OrderDetailsPOJO(OrderDetails od) {
        this.number = od.getNumber().toString();
        this.consumptionItems = od.getConsumptionItems();
        this.employeesId = od.getEmployeesId();
        this.name1 = od.getName1();
        this.phPrefix1 = od.getPhPrefix1();
        this.phone1 = od.getPhone1();
        this.companyId = od.getCompanyId();
        this.invoiceName = od.getInvoiceName();
        this.invoiceNumber = od.getInvoiceNumber();
        this.customerId = od.getCustomerId();
        this.name2 = od.getName2();
        this.phPrefix2 = od.getPhPrefix2();
        this.phone2 = od.getPhone2();
        this.jobIds = od.getJobIds();
        this.address = od.getAddress();
        this.lng = od.getLng();
        this.lat = od.getLat();
        this.days = od.getDays();
        this.hOfDay = od.getHOfDay();
        this.priceBeforeDiscount = od.getPriceBeforeDiscount();
        this.priceAfterDiscount = od.getPriceAfterDiscount();
        this.payType = od.getPayType();
        this.remarks = od.getRemarks();
        this.startDateTime = od.getStartDateTime();
        this.updateDateTime = od.getUpdateDateTime();
        this.h = od.getH();
        this.payDeadline = od.getPayDeadline();
        this.orderState = od.getOrderState();
        this.orderOrigin = od.getOrderOrigin();
        this.payDateTime = od.getPayDateTime();
        this.completionDateTime = od.getCompletionDateTime();
        this.fixDateTime = od.getFixDateTime();
        this.evaluationDateTime = od.getEvaluationDateTime();
    }
}
