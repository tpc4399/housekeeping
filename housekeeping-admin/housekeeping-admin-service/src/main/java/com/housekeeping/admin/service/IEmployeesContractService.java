package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/1/30 17:16
 */
public interface IEmployeesContractService extends IService<EmployeesContract> {

    R add(AddEmployeesContractDTO dto);

}
