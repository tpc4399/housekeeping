package com.housekeeping.auth.service;

import com.housekeeping.common.utils.R;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author su
 * @create 2020/10/28 18:12
 */
public interface ILoginService {
    R loginByEmailAndPasswordHandle(String email, String password);
    R loginByPhoneAndPasswordHandle(String phone, String password);
    R sendLoginSMSMessage(String phone);
    R loginByPhoneAndCodeHandle(String phone, String code);
    R changePw(String newPassword, HttpServletRequest request);
}
