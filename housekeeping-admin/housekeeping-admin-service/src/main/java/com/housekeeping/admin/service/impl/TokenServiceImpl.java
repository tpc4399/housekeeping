package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.service.TokenService;
import com.housekeeping.common.entity.HkUser;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {


    @Override
    public String getToken(HkUser hkUser) {
        return TokenUtils.getToken(hkUser);
    }
}
