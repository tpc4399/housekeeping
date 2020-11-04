package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.User;
import com.housekeeping.common.utils.R;

public interface IUserService extends IService<User> {
    User getUserByEmail(String email);
    User getUserByPhone(String phone);

    Boolean checkData(String data, Integer type);

    R sendRegisterMSMessage(String phone);
}
