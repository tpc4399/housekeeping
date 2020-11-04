package com.housekeeping.auth.service.impl;

import com.housekeeping.auth.mapper.HkUserMapper;
import com.housekeeping.auth.mapper.UserMapper;
import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.auth.service.ITokenService;
import com.housekeeping.common.utils.DESEncryption;
import com.housekeeping.common.utils.RedisUtils;
import com.housekeeping.common.entity.HkUser;

import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author su
 * @create 2020/10/28 18:13
 */
@Service("loginService")
public class LoginService implements ILoginService {

    @Resource
    private HkUserMapper hkUserMapper;

    @Resource
    private ITokenService tokenService;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public R loginByEmailAndPasswordHandle(String email, String password, Integer deptId) {
        HkUser hkUser = hkUserMapper.byEmail(email, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
            if (CommonUtils.isNotEmpty(password)) {
                if (DESEncryption.getEncryptString(password).equals(hkUser.getPassword())) {
                    hkUser.setAuthType(0);
                    //获取token，生成token，返回token
                    return R.ok(tokenService.getToken(hkUser), "登入成功");
                }
            }
        }

        return R.failed("郵箱號未註冊或密碼錯誤");
    }

    @Override
    public R loginByPhoneAndPasswordHandle(String phone, String password, Integer deptId) {
        HkUser hkUser = hkUserMapper.byPhone(phone, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
            if (CommonUtils.isNotEmpty(password)) {
                if (DESEncryption.getEncryptString(password).equals(hkUser.getPassword())) {
                    hkUser.setAuthType(1);
                    //获取token，生成token，返回token
                    return R.ok(tokenService.getToken(hkUser), "登入成功");
                }
            }
        }

        return R.failed("手機號未註冊或密碼錯誤");
    }

    @Override
    public R sendLoginSMSMessage(String phone, Integer deptId) {
        HkUser hkUser = hkUserMapper.byPhone(phone, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
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
    public R loginByPhoneAndCodeHandle(String phone, String code, Integer deptId) {
        HkUser hkUser = hkUserMapper.byPhone(phone, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
            if (CommonUtils.isNotEmpty(code)) {
                //判斷redis中的驗證碼是否正確
                if (code.equals(redisUtils.get(CommonConstants.LOGIN_KEY_BY_PHONE + deptId + phone))) {
                    hkUser.setAuthType(2);
                    //获取token，生成token，返回token
                    return R.ok(tokenService.getToken(hkUser), "登入成功");
                }else {
                    return R.failed("驗證碼錯誤");
                }
            }else {
                return R.failed("請輸入驗證碼");
            }
        }else {
            return R.failed("手機號未註冊");
        }
    }

    @Override
    public R changePw(String newPassword, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        com.housekeeping.common.entity.HkUser hkUser = TokenUtils.parsingToken(token);
        String phone = hkUser.getPhone();
        String newPasswordEn = DESEncryption.getEncryptString(newPassword);
        userMapper.changePasswordByPhone(phone, newPasswordEn);
        return R.ok("修改密碼成功");
    }

}
