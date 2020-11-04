package com.housekeeping.auth.service;


import com.housekeeping.common.entity.HkUser;

/**
 * @Author su
 * @create 2020/10/27 2:27
 */
public interface IHkUserService {
    HkUser byEmail(String email, Integer deptId);
    HkUser byPhone(String phone, Integer deptId);



}
