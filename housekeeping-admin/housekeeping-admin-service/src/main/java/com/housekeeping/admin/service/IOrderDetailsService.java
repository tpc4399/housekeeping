package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author su
 * @Date 2021/4/19 9:46
 */
public interface IOrderDetailsService extends IService<OrderDetails> {

    /* 公司的订单保留时长 */
    Integer orderRetentionTime(Integer employeesId);

    /*  */
    R pay(Long number,
          Integer employeesId,
          MultipartFile[] photos,
          String[] evaluates,
          String payType,
          String remarks) throws Exception;

}
