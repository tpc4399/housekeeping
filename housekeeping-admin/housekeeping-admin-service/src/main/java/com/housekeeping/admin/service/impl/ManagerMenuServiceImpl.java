package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.ManagerMenu;
import com.housekeeping.admin.mapper.ManagerMenuMapper;
import com.housekeeping.admin.service.IManagerMenuService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/1/13 17:28
 */
@Service("managerMenuService")
public class ManagerMenuServiceImpl
        extends ServiceImpl<ManagerMenuMapper, ManagerMenu>
        implements IManagerMenuService {
}
