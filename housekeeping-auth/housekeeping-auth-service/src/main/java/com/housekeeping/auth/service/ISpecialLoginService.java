package com.housekeeping.auth.service;

import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/10 16:39
 */
public interface ISpecialLoginService {
    R authEmployees(String key);
    R authManager(String key);
}
