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
    public R loginByPhoneAndPasswordHandle(String phonePrefix, String phone, String password, Integer deptId) {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        HkUser hkUser = hkUserMapper.byPhoneLogin(phonePrefix,phone, deptId);
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
    public R sendLoginSMSMessage(String phonePrefix, String phone, Integer deptId) throws Exception {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        HkUser hkUser = hkUserMapper.byPhone(phonePrefix, phone, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.LOGIN_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage(phonePrefix, phone, params);
            return R.ok("成功發送短信");
        }else {
            return R.failed("該手機號未註冊");
        }
    }

    @Override
    public R loginByPhoneAndCodeHandle(String phonePrefix, String phone, String code, Integer deptId) {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        HkUser hkUser = hkUserMapper.byPhone(phonePrefix, phone, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
            if (CommonUtils.isNotEmpty(code)) {
                //判斷redis中的驗證碼是否正確String key = CommonConstants.LOGIN_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone;
                if (code.equals(redisUtils.get(CommonConstants.LOGIN_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone))) {
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
    public R changePw(String newPassword, String rePassword) {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        if(!newPassword.equals(rePassword)){
            return R.failed("两次密码不一致");
        }else {
            String encryptString = DESEncryption.getEncryptString(newPassword);
            userMapper.updatePassword(currentUserId, encryptString);
            return R.ok("密码修改成功");
        }
    }

    @Override
    public R  loginByPhoneAndPassword(String phonePrefix, String phone, String password) {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        HkUser hkUser = hkUserMapper.loginIn(phone);
        if (CommonUtils.isNotEmpty(hkUser)) {
            if (CommonUtils.isNotEmpty(password)) {
                if (DESEncryption.getEncryptString(password).equals(hkUser.getPassword())) {
                    hkUser.setAuthType(1);
                    //获取token，生成token，返回token
                    return R.ok(tokenService.getToken(hkUser), "登入成功");
                }else{
                    R.failed("密碼錯誤");
                }
            }
        }else{
            return R.failed("手機號未注冊，請先注冊!");
        }
        return R.failed("手機號或密碼錯誤");
    }

    @Override
    public R sendLoginSMS(String phonePrefix, String phone) throws Exception {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        HkUser hkUser = hkUserMapper.byPwdLogin(phonePrefix, phone);
        if (CommonUtils.isNotEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.LOGIN_KEY_BY_PHONE  + "_+" + phonePrefix + "_" +  phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage(phonePrefix, phone, params);
            return R.ok("成功發送短信");
        }else {
            return R.failed("該手機號未註冊");
        }
    }

    @Override
    public R loginByPhoneAndCode(String phonePrefix, String phone, String code) {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        HkUser hkUser = hkUserMapper.byPwdLogin(phonePrefix, phone);
        if (CommonUtils.isNotEmpty(hkUser)) {
            if (CommonUtils.isNotEmpty(code)) {
                //判斷redis中的驗證碼是否正確String key = CommonConstants.LOGIN_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone;
                if (code.equals(redisUtils.get(CommonConstants.LOGIN_KEY_BY_PHONE + "_+" + phonePrefix + "_" +  phone))) {
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
    public R loginByPhoneAndPasswordHandleAdmin(String phonePrefix, String phone, String password, int deptId) {
        HkUser hkUser = hkUserMapper.byPhoneLogin2(phone, deptId);
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


}
