package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.User;
import com.housekeeping.common.utils.R;

public interface IUserService extends IService<User> {
    User getUserByEmail(String email,Integer deptId);
    User getUserByPhone(String phone,Integer deptId);

    Boolean checkData(String data, Integer type);

    R sendRegisterMSMessage(String phone,Integer deptId);

    R saveEmp(RegisterDTO registerDTO);

    R saveCus(RegisterDTO registerDTO);

    R saveAdmin(RegisterDTO registerDTO);
}
