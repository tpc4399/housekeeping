package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.PaymentCallback;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.dto.SmilePayVerificationCodeDTO;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/19 9:46
 */
public interface IOrderDetailsService extends IService<OrderDetails> {

    /* 公司的订单保留时长 */
    Integer orderRetentionTime(Integer employeesId);

    /* 客户请求修改地址操作，更新保洁员通知列表和聊天消息 */
    R requestToChangeAddress(RequestToChangeAddressDTO dto);

    /* 保洁员同意或拒绝修改地址信息 */
    R requestToChangeAddressHandle(Integer id, Boolean result);

    /* 修改待付款订单信息时候，需要更新修改时间和订单付款截止时间 */
    R updateOrder(Integer number, Integer employeesId);

    /* 支付操作需要调用，保存照片，描述，支付方式和备注 */
    R pay(Long number,
          Integer employeesId,
          MultipartFile[] photos,
          String[] evaluates,
          String payType,
          String remarks) throws Exception;

    /* 支付回调处理 */
    void paymentCallback(PaymentCallback pc);

    /* 存储支付回调信息 */
    void savePCInfo(PaymentCallback pc);

    /* 参数验证 */
    Boolean smilePayVerificationCode(SmilePayVerificationCodeDTO dto);

    /* 订单保存到mysql，作永久存储,用于订单状态变为"已支付"状态调用
    * true 变为已支付的订单 --> 待服务
    * false 变为已取消的订单 --> 已取消
    * 該方法只是单纯地改状态，并不涉及其它字段的修改
    * */
    R inputSql(String number, Boolean status);

    /* 订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
    R queryByCus(Integer type);

    /* 订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
    R queryByEmp(Integer type);

    /* 公司查询本公司订单 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
    R queryByCom(Integer type);

    /* 查询保洁员的待付款订单 */
    List<OrderDetailsPOJO> order1ByEmployees(Integer employeesId);
    /* 查询保洁员的待服务订单 */
    List<OrderDetailsPOJO> order2ByEmployees(Integer employeesId);
    /* 查询保洁员的进行中订单 */
    List<OrderDetailsPOJO> order3ByEmployees(Integer employeesId);
    /* 查询保洁员的待评价订单 */
    List<OrderDetailsPOJO> order4ByEmployees(Integer employeesId);
    /* 查询保洁员的已完成订单 */
    List<OrderDetailsPOJO> order5ByEmployees(Integer employeesId);
    /* 查询客户的待付款订单 */
    List<OrderDetailsPOJO> order1ByCustomer(Integer customerId);
    /* 查询客户的待服务订单 */
    List<OrderDetailsPOJO> order2ByCustomer(Integer customerId);
    /* 查询客户的进行中订单 */
    List<OrderDetailsPOJO> order3ByCustomer(Integer customerId);
    /* 查询客户的待评价订单 */
    List<OrderDetailsPOJO> order4ByCustomer(Integer customerId);
    /* 查询客户的已完成订单 */
    List<OrderDetailsPOJO> order5ByCustomer(Integer customerId);
    /* 查询公司的待付款订单 */
    List<OrderDetailsPOJO> order1ByEmployeesAll(List<Integer> empIds);
    /* 查询公司的待服务订单 */
    List<OrderDetailsPOJO> order2ByEmployeesAll(List<Integer> empIds);
    /* 查询公司的进行中订单 */
    List<OrderDetailsPOJO> order3ByEmployeesAll(List<Integer> empIds);
    /* 查询公司的待评价订单 */
    List<OrderDetailsPOJO> order4ByEmployeesAll(List<Integer> empIds);
    /* 查询公司的已完成订单 */
    List<OrderDetailsPOJO> order5ByEmployeesAll(List<Integer> empIds);

    /* 【保洁员】【客户】取消订单,将订单放入取消的订单列表 */
    R payment1();
    /* 【保洁员】订单状态———— 处理中->未支付 */
    R payment2(String number);
    /* 【保洁员】订单状态———— 进行中->待评价 */
    R payment3(String number);
    /* 【客户】评价订单———— 进行中->待评价 */
    R payment4();
    /* 【保洁员】订单误判———— 已取消订单->已支付 */
    R payment5();
    /* 【保洁员】【客户】根据订单编号，查看是否属于调用者 或者管理者 */
    Boolean orderVerification(OrderDetailsPOJO odp);
    /* 根据订单编号获取订单的全部 */
    OrderDetailsPOJO odp(String number);
    /* 根據訂單編號獲取訂單狀態 */
    Integer getState(String number);
    /* 根据number获取订单详情 */
    R getOrder(String number);
}
