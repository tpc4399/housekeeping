package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @create 2020/11/19 14:55
 */
public interface IGroupEmployeesService extends IService<GroupEmployees> {

    R add(@RequestBody GroupEmployeesDTO groupEmployeesDTO);

    R delete(@RequestBody GroupEmployeesDTO groupEmployeesDTO);

    R matchTheEmployees(Integer managerId);

    R getAllEmp(GroupDTO groupDTO);

    R getAllEmpById(Integer groupId);
}
