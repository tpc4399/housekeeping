package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.DemandEmployees;

import java.util.List;

/**
 * @Author su
 * @Date 2021/2/22 16:28
 */
public interface DemandEmployeesMapper extends BaseMapper<DemandEmployees> {
    List<Integer> getAllDemandIds(Integer userId);

    List<Integer> getAllUserId();

    List<Integer> getAllDemandIdsByEmpId(Integer employeesId);
}
