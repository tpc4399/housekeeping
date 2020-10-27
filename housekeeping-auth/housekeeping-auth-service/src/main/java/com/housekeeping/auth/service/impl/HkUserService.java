package com.housekeeping.auth.service.impl;

import com.housekeeping.auth.mapper.HkUserMapper;
import com.housekeeping.auth.service.IHkUserService;
import com.housekeeping.auth.utils.HkUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2020/10/27 2:27
 */
@Service("hkUserService")
public class HkUserService implements IHkUserService {

    @Autowired
    private HkUserMapper hkUserMapper;

    @Override
    public HkUser byEmail(String email) {
        return hkUserMapper.byEmail(email);
    }

    @Override
    public HkUser byPhone(String phone) {
        return hkUserMapper.byPhone(phone);
    }
}
