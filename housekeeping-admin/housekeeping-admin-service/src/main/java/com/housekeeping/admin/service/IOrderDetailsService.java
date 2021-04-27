package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.PaymentCallbackDTO;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.dto.SmilePayVerificationCodeDTO;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    String paymentCallback(PaymentCallbackDTO dto) throws IOException;

    Boolean smilePayVerificationCode(SmilePayVerificationCodeDTO dto);

}
