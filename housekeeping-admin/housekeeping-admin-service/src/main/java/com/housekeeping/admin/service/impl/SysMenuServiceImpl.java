package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysMenu;
import com.housekeeping.admin.mapper.SysMenuMapper;
import com.housekeeping.admin.service.ISysMenuService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/1/13 17:26
 */
@Service("sysMenuService")
public class SysMenuServiceImpl
        extends ServiceImpl<SysMenuMapper, SysMenu>
        implements ISysMenuService {
}
