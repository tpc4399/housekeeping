package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.Serial;
import com.housekeeping.admin.mapper.SerialMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.service.ISerialService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2021/5/26 9:44
 */
@Service("serialService")
public class SerialServiceImpl
        extends ServiceImpl<SerialMapper, Serial>
        implements ISerialService {
    @Override
    public R generatePipeline(OrderDetailsPOJO od) {
        return null;
    }
}
