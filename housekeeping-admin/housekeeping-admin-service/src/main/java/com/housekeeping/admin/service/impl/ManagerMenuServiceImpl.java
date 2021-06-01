package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.UpdateManagerMenuDTO;
import com.housekeeping.admin.entity.ManagerMenu;
import com.housekeeping.admin.entity.SysMenu;
import com.housekeeping.admin.mapper.ManagerMenuMapper;
import com.housekeeping.admin.service.IManagerMenuService;
import com.housekeeping.admin.service.ISysMenuService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2021/1/13 17:28
 */
@Service("managerMenuService")
public class ManagerMenuServiceImpl
        extends ServiceImpl<ManagerMenuMapper, ManagerMenu>
        implements IManagerMenuService {

    @Resource
    private ISysMenuService sysMenuService;

    @Override
    public R updateManagerMenu(UpdateManagerMenuDTO dto) {
        Integer managerId = dto.getManagerId();
        //删
        QueryWrapper qw = new QueryWrapper();
        qw.eq("manager_id", managerId);
        this.remove(qw);
        //增
        List<ManagerMenu> managerMenus = new ArrayList<>();
        dto.getMenuIds().forEach(x -> {
            ManagerMenu managerMenu = new ManagerMenu();
            managerMenu.setManagerId(managerId);
            managerMenu.setMenuId(x);
            managerMenus.add(managerMenu);
        });
        this.saveBatch(managerMenus);
        return R.ok("成功修改經理菜單");
    }

    @Override
    public R getManagerMenu(Integer managerId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("manager_id", managerId);
        List<ManagerMenu> managerMenus = this.list(qw);
        List<Integer> menuIds = managerMenus.stream().map(x -> {
            return x.getMenuId();
        }).collect(Collectors.toList());
        List<SysMenu> sysMenuList = new ArrayList<>();
        if (!menuIds.isEmpty()) sysMenuList = sysMenuService.listByIds(menuIds);
        return R.ok(sysMenuList, "獲取經理菜單成功");
    }
}
