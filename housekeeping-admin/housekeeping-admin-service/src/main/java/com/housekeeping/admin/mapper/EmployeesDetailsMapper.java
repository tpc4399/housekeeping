package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.EmployeesDetails;

public interface EmployeesDetailsMapper extends BaseMapper<EmployeesDetails> {

    public String getScaleById(Integer id);
}
