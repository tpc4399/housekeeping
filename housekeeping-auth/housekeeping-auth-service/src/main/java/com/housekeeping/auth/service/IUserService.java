package com.housekeeping.auth.service;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.admin.entity.User;
import com.housekeeping.common.utils.R;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author su
 * @create 2020/10/28 17:12
 */
public interface IUserService {
    R register(UserDTO userDTO);
    void bindingEmailByUserId(Integer userId, String email);
    Integer getDeptIdByUserId(Integer userId);
    User getOne(Integer deptId, String email);

    R checkPw(String password);

    R sendSms();

    R checkCode(String code);

    R newPhone(String phone,String phonePrefix);

    R checkCodeByNewPhone(String code,String phone,String phonePrefix);

    R checkToken(HttpServletRequest request);
}
