package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/19 14:55
 */
public interface IGroupEmployeesService extends IService<GroupEmployees> {

    R matchTheEmployees(Integer managerId);

}
