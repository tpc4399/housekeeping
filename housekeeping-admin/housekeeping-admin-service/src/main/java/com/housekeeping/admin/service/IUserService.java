package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.User;
import com.housekeeping.common.utils.R;

public interface IUserService extends IService<User> {

    User getUserByPhone(String phonePrefix,String phone,Integer deptId);

    R checkData(Integer deptId,String data, Integer type);

    R sendRegisterMSMessage(String phonePrefix,String phone,Integer deptId);

    R saveEmp(RegisterDTO registerDTO);

    R saveCus(RegisterDTO registerDTO);

    R saveAdmin(RegisterDTO registerDTO);
}
