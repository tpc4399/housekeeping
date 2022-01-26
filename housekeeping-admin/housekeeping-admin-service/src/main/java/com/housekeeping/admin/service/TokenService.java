package com.housekeeping.admin.service;

import com.housekeeping.common.entity.HkUser;

public interface TokenService {
    public String getToken(HkUser hkUser);
}
