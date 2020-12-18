package com.housekeeping.auth.service;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.common.utils.R;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @create 2020/10/28 17:12
 */
public interface IUserService {
    R register(UserDTO userDTO);
    void bindingEmailByUserId(Integer userId, String email);
}
