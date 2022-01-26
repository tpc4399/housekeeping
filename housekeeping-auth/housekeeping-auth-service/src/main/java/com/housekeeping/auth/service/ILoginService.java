package com.housekeeping.auth.service;

import com.housekeeping.common.utils.R;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author su
 * @create 2020/10/28 18:12
 */
public interface ILoginService {
    R loginByEmailAndPasswordHandle(String email, String password, Integer deptId);
    R loginByPhoneAndPasswordHandle(String phonePrefix, String phone, String password, Integer deptId);
    R sendLoginSMSMessage(String phonePrefix, String phone, Integer deptId) throws Exception;
    R loginByPhoneAndCodeHandle(String phonePrefix, String phone, String code, Integer deptId);
    R changePw(String newPassword, String rePassword);

    R loginByPhoneAndPassword(String phonePrefix, String phone, String password);

    R sendLoginSMS(String phonePrefix, String phone) throws Exception;

    R loginByPhoneAndCode(String phonePrefix, String phone, String code);

    R loginByPhoneAndPasswordHandleAdmin(String phonePrefix, String phone, String password, int deptId);
}
