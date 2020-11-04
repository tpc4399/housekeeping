package com.housekeeping.auth.service;

import com.housekeeping.common.utils.R;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author su
 * @create 2020/10/28 18:12
 */
public interface ILoginService {
    R loginByEmailAndPasswordHandle(String email, String password, Integer deptId);
    R loginByPhoneAndPasswordHandle(String phone, String password, Integer deptId);
    R sendLoginSMSMessage(String phone, Integer deptId);
    R loginByPhoneAndCodeHandle(String phone, String code, Integer deptId);
    R changePw(String newPassword, HttpServletRequest request);
}
