package com.housekeeping.auth.service.impl;

import com.housekeeping.auth.mapper.HkUserMapper;
import com.housekeeping.auth.service.ILoginService;
import com.housekeeping.auth.service.ITokenService;
import com.housekeeping.auth.utils.DESEncryption;
import com.housekeeping.auth.utils.HkUser;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2020/10/28 18:13
 */
@Service("loginService")
public class LoginService implements ILoginService {

    @Autowired
    private HkUserMapper hkUserMapper;

    @Autowired
    private ITokenService tokenService;

    @Override
    public R loginByEmailAndPasswordHandle(String email, String password) {
        HkUser hkUser = hkUserMapper.byEmail(email);
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
    public R loginByPhoneAndPasswordHandle(String phone, String password) {
        HkUser hkUser = hkUserMapper.byPhone(phone);
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
    public R loginByPhoneAndCodeHandle(String phone, String code) {
        HkUser hkUser = hkUserMapper.byPhone(phone);
        if (CommonUtils.isNotEmpty(hkUser)) {
            if (CommonUtils.isNotEmpty(code)) {
                //判斷redis中的驗證碼是否正確
                if (true) {
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
}
