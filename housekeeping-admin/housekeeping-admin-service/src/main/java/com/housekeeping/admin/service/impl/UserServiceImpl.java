package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.UserMapper;
import com.housekeeping.admin.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("email", email);
        qr.eq("del_flag", 0); //未删除
        User res = baseMapper.selectOne(qr);

        return res;
    }

    @Override
    public User getUserByPhone(String phone) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone", phone);
        qr.eq("del_flag", 0); //未删除
        User res = baseMapper.selectOne(qr);

        return res;
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper qr = new QueryWrapper();
        switch (type){
            case 1:
                qr.eq("phone", data);
                qr.eq("del_flag", 0); //未删除
                break;
            case 2 :
                qr.eq("email", data);
                qr.eq("del_flag", 0); //未删除
                break;
        }
        return this.userMapper.selectCount(qr) == 0;
    }
}
