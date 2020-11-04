package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.UserMapper;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisUtils redisUtils;

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

    @Override
    public R sendRegisterMSMessage(String phone) {
        User hkUser = this.getUserByPhone(phone);
        if (CommonUtils.isEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.REGISTER_KEY_BY_PHONE + phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage("86", phone, params);
            return R.ok("成功發送短信");
        }else {
            return R.failed("該手機號為註冊");
        }
    }


}
