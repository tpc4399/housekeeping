package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SerialWorks;
import com.housekeeping.admin.mapper.SerialWorksMapper;
import com.housekeeping.admin.service.ISerialWorksService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2021/5/26 11:55
 */
@Service("serialWorksService")
public class SerialWorksServiceImpl
        extends ServiceImpl<SerialWorksMapper, SerialWorks>
        implements ISerialWorksService {
}
