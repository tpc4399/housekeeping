package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.UserMapper;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public R getUserByEmail(String email) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("email", email);
        qr.eq("del_flag", 0); //未删除
        List<User> userList = baseMapper.selectList(qr);

        return CommonUtils.selectOneHandle(userList, "邮箱号错误或者根本没有此邮箱", "通过邮箱获取用户成功", "重大问题：数据库数据有误，存在多个用户使用了该邮箱");
    }

    @Override
    public R getUserByPhone(String phone) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone", phone);
        qr.eq("del_flag", 0); //未删除
        List<User> userList = baseMapper.selectList(qr);

        return CommonUtils.selectOneHandle(userList, "手机号错误或者根本没有此手机号", "通过手机号获取用户成功", "重大问题：数据库数据有误，存在多个用户使用了该手机号");
    }
}
