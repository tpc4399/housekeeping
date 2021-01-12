package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysIndexContent;
import com.housekeeping.admin.mapper.SysIndexContentMapper;
import com.housekeeping.admin.service.ISysIndexContentService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/1/12 14:46
 */
@Service("sysIndexContentService")
public class SysIndexContentServiceImpl
        extends ServiceImpl<SysIndexContentMapper, SysIndexContent>
        implements ISysIndexContentService {
}
