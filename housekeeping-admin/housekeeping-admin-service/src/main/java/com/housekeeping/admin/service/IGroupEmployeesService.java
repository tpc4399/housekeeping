package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.GroupAdminDTO;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupEmployeesAdminDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.admin.vo.EmployeesDetailsSkillVo;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/19 14:55
 */
public interface IGroupEmployeesService extends IService<GroupEmployees> {

    R save(GroupEmployeesDTO groupEmployeesDTO);

    R matchTheEmployees(Integer managerId);

    R getAllEmp(GroupDTO groupDTO);

    List<EmployeesDetailsSkillVo> getAllEmpById(Integer groupId);

    R getAllEmpByAdmin(GroupAdminDTO groupDTO);

    R saveByAdmin(GroupEmployeesAdminDTO groupEmployeesDTO);

    /* 經理 獲取自己旗下保潔員的ids */
    @Access(RolesEnum.USER_MANAGER)
    List<Integer> getEmployeesIdsByManager();

    /* 公司查看經理旗下保潔員的ids */
    List<Integer> getEmployeesIdsByManagerId(Integer manId);
}
