package com.housekeeping.auth.service.impl;

import com.housekeeping.auth.mapper.HkUserMapper;
import com.housekeeping.auth.service.IHkUserService;
import com.housekeeping.common.entity.HkUser;
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
    public HkUser byEmail(String email, Integer deptId) {
        HkUser res = hkUserMapper.byEmail(email, deptId);
        return res;
    }

    @Override
    public HkUser byPhone(String phonePrefix, String phone, Integer deptId) {
        return hkUserMapper.byPhone(phonePrefix, phone, deptId);
    }

}
