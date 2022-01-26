package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2021/4/19 9:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_details")
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetails extends Model<OrderDetails> {

    private Long number;                        //订单编号
    private String serialNumber;                //流水号
    private String consumptionItems;            //消费项目

    private Integer employeesId;                //订单甲方 保洁员 (一个)
    private String name1;                       //甲方姓名
    private String phPrefix1;                   //甲方手机号前缀
    private String phone1;                      //甲方手机号
    private String companyId;                  //甲方所属公司
    private String invoiceName;                 //公司发票抬头
    private String invoiceNumber;               //公司统一编号

    private Integer customerId;                 //订单乙方 客户 (一个)
    private String name2;                       //乙方姓名
    private String phPrefix2;                   //乙方手机号前缀
    private String phone2;                      //乙方手机号

    private String jobIds;                      //工作内容
    private String noteIds;                     //工作筆記
    private String address;                     //服务地址
    private Float lng;                          //经度
    private Float lat;                          //纬度

    private BigDecimal platformFeeCus;          //平台費客戶
    private BigDecimal platformFeeEmp;          //平台費員工

    private BigDecimal bonus;                   //订单佣金

    private BigDecimal matchmakingFee;          //媒合费
    private BigDecimal systemServiceFee;        //系统服务费
    private BigDecimal cardSwipeFee;            //刷卡手续费
    private Integer days;                       //天数
    private Float hOfDay;                       //每天的小时数
    private BigDecimal priceBeforeDiscount;     //优惠前的价格(台币元)
    private BigDecimal priceAfterDiscount;      //优惠后的价格(台币元)
    private String payType;                     //支付方式
    private String remarks;                     //备注
    private LocalDateTime startDateTime;        //订单生成时间
    private LocalDateTime updateDateTime;       //订单最后修改时间
    private Integer h;                          //订单保留时间
    private LocalDateTime payDeadline;          //订单付款截止时间

    private Integer orderState;                 //订单状态
    private Integer orderOrigin;                //订单来源 0钟点工 1包工
    private String contractImageUrl;            //包工图片
    private String contractName;                //包工名

    private LocalDateTime payDateTime;          //付款时间
    private LocalDateTime completionDateTime;   //完成时间
    private LocalDateTime fixDateTime;          //确认时间
    private LocalDateTime evaluationDateTime;   //评价时间

    public OrderDetails(OrderDetailsPOJO pojo) {
        this.number = Long.valueOf(pojo.getNumber());
        this.serialNumber = pojo.getSerialNumber();
        this.consumptionItems = pojo.getConsumptionItems();
        this.employeesId = pojo.getEmployeesId();
        this.name1 = pojo.getName1();
        this.phPrefix1 = pojo.getPhPrefix1();
        this.phone1 = pojo.getPhone1();
        this.companyId = pojo.getCompanyId();
        this.invoiceName = pojo.getInvoiceName();
        this.invoiceNumber = pojo.getInvoiceNumber();
        this.customerId = pojo.getCustomerId();
        this.name2 = pojo.getName2();
        this.phPrefix2 = pojo.getPhPrefix2();
        this.phone2 = pojo.getPhone2();
        this.jobIds = pojo.getJobIds();
        this.noteIds = pojo.getNoteIds();
        this.address = pojo.getAddress();
        this.platformFeeCus = pojo.getPlatformFeeCus();
        this.platformFeeEmp = pojo.getPlatformFeeEmp();
        this.matchmakingFee = pojo.getMatchmakingFee();
        this.systemServiceFee = pojo.getSystemServiceFee();
        this.cardSwipeFee = pojo.getCardSwipeFee();
        this.lng = pojo.getLng();
        this.lat = pojo.getLat();
        this.days = pojo.getDays();
        this.hOfDay = pojo.getHOfDay();
        this.priceBeforeDiscount = pojo.getPriceBeforeDiscount();
        this.priceAfterDiscount = pojo.getPriceAfterDiscount();
        this.payType = pojo.getPayType();
        this.remarks = pojo.getRemarks();
        this.startDateTime = pojo.getStartDateTime();
        this.updateDateTime = pojo.getUpdateDateTime();
        this.h = pojo.getH();
        this.payDeadline = pojo.getPayDeadline();
        this.orderState = pojo.getOrderState();
        this.orderOrigin = pojo.getOrderOrigin();
        this.contractImageUrl = pojo.getContractImageUrl();
        this.contractName = pojo.getContractName();
        this.payDateTime = pojo.getPayDateTime();
        this.completionDateTime = pojo.getCompletionDateTime();
        this.fixDateTime = pojo.getFixDateTime();
        this.evaluationDateTime = pojo.getEvaluationDateTime();
    }
}
