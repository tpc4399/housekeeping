package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.UpdateManagerMenuDTO;
import com.housekeeping.admin.entity.ManagerMenu;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @Date 2021/1/13 17:25
 */
public interface IManagerMenuService extends IService<ManagerMenu> {

    R updateManagerMenu(UpdateManagerMenuDTO dto);
    R getManagerMenu(Integer managerId);

}
