package com.housekeeping.auth.mapper;

import com.housekeeping.common.entity.HkUser;

/**
 * @Author su
 * @create 2020/10/27 2:21
 */
public interface HkUserMapper {
    HkUser byEmail(String email);
    HkUser byPhone(String phone);
}
