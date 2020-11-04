package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.UserMapper;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.entity.HkUser;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public User getUserByEmail(String email,Integer deptId) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("dept_id",deptId);
        qr.eq("email", email);
        qr.eq("del_flag", 0); //未删除
        User res = baseMapper.selectOne(qr);

        return res;
    }

    @Override
    public User getUserByPhone(String phone,Integer deptId) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("dept_id",deptId);
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
    public R sendRegisterMSMessage(String phone,Integer deptId) {
        User hkUser = this.getUserByPhone(phone,deptId);
        if (CommonUtils.isEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.LOGIN_KEY_BY_PHONE + deptId + phone;
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

    @Override
    public R saveEmp(RegisterDTO registerDTO) {
        return null;
    }

    @Override
    public R saveCus(RegisterDTO registerDTO) {
        return null;
    }

    @Override
    public R saveAdmin(RegisterDTO registerDTO) {
        if(CommonUtils.isNotEmpty(registerDTO)){
            if (CommonUtils.isNotEmpty(registerDTO.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (registerDTO.getCode().equals(redisUtils.get(CommonConstants.LOGIN_KEY_BY_PHONE + 1 + registerDTO.getPhone()))){
                    if(registerDTO.getPassword().equals(registerDTO.getRepassword())){
                        User user = new User();
                        user.setNumber(String.valueOf(System.currentTimeMillis()));
                        user.setDeptId(1);
                        user.setName(registerDTO.getName());
                        user.setPhone(registerDTO.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(registerDTO.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        userMapper.insert(user);
                    }else {
                        return R.ok("两次密码不一致");
                    }
                }else {
                    return R.failed("验证码错误");
                }
            }else {
                return R.failed("验证码为空");
            }
        }
        return R.ok("创建平台管理员成功");
    }


}
