package com.housekeeping.auth.service;


import com.housekeeping.common.entity.HkUser;

/**
 * @Author su
 * @create 2020/10/27 3:23
 */
public interface ITokenService {
    String getToken(HkUser hkUser);
}
