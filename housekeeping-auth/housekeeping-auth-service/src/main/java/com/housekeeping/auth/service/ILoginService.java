package com.housekeeping.auth.service;

import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/10/28 18:12
 */
public interface ILoginService {
    R loginByEmailAndPasswordHandle(String email, String password);
    R loginByPhoneAndPasswordHandle(String phone, String password);
    R loginByPhoneAndCodeHandle(String phone, String code);
}
