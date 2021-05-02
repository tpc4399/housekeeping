package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.PaymentCallbackDTO;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.dto.SmilePayVerificationCodeDTO;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.common.utils.R;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    void paymentCallback(PaymentCallbackDTO dto);

    /* 参数验证 */
    Boolean smilePayVerificationCode(SmilePayVerificationCodeDTO dto);

    /* 订单保存到mysql，作永久存储,用于订单状态变为"已支付"状态调用 */
    Long toBePaid(Long number, Integer employeesId);

    /* 订单查询 type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
    R query(Integer type);

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

}
