package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.admin.mapper.SysIndexMapper;
import com.housekeeping.admin.service.ISysIndexService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/1/12 14:48
 */
@Service("sysIndexService")
public class SysIndexServiceImpl
        extends ServiceImpl<SysIndexMapper, SysIndex>
        implements ISysIndexService {
}
