package com.housekeeping.admin.service;

import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2021/5/26 9:44
 */
public interface ISerialService {

    /* 生成流水 */
    R generatePipeline(OrderDetailsPOJO od);

}
