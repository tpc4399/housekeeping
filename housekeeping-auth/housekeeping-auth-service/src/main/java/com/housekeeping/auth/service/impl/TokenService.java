package com.housekeeping.auth.service.impl;

import com.housekeeping.auth.service.ITokenService;
import com.housekeeping.auth.utils.HkUser;
import com.housekeeping.auth.utils.TokenUtils;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2020/10/27 3:24
 */
@Service("tokenService")
public class TokenService implements ITokenService {
    @Override
    public String getToken(HkUser hkUser) {
        return TokenUtils.getToken(hkUser);
    }
}
