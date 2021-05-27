package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2021/5/26 9:44
 */
public interface ISerialService {

    /* 保存流水 */
    R generatePipeline(OrderDetailsPOJO od);

    /* 分頁查詢流水 */
    R pageOfSerial(Page page);

}
