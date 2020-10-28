package com.housekeeping.auth.service;


import com.housekeeping.auth.utils.HkUser;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/10/27 2:27
 */
public interface IHkUserService {
    HkUser byEmail(String email);
    HkUser byPhone(String phone);



}
