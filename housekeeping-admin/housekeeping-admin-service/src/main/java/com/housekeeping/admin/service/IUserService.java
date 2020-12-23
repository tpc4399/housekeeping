package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.AdminPageDTO;
import com.housekeeping.admin.dto.ForgetDTO;
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

    R sendForgetMSMessage(String phonePrefix, String phone, Integer deptId);

    R updatePwd(ForgetDTO forgetDTO);

    R verfifyCode(String phonePrefix, String phone,String code,Integer deptId);

    R getAllUser(IPage<User> page, AdminPageDTO adminPageDTO,Integer deptId);

    User getUserByIdAndDept(Integer id, int i);
}
