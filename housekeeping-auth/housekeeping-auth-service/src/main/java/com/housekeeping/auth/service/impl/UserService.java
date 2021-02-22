package com.housekeeping.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.admin.entity.User;
import com.housekeeping.auth.mapper.HkUserMapper;
import com.housekeeping.auth.mapper.UserMapper;
import com.housekeeping.auth.service.IUserService;
import com.housekeeping.common.entity.HkUser;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import jdk.nashorn.internal.parser.Token;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.netflix.zuul.context.RequestContext;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author su
 * @create 2020/10/28 17:13
 */
@Service("userService")
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HkUserService hkUserService;

    @Resource
    private HkUserMapper hkUserMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public R register(UserDTO userDTO) {
        //判空
        //生成number
        //生成lastReviserId
        //密碼加密
        //保證手機號郵箱唯一
        if (CommonUtils.isEmpty(userDTO.getPhone()) || CommonUtils.isEmpty(userDTO.getPassword())){
            return R.failed("必填項為空");
        }
        if (CommonUtils.isNotEmpty(hkUserService.byPhone(userDTO.getPhonePrefix(), userDTO.getPhone(), userDTO.getDeptId()))){
            return R.failed("手機號已存在");
        }
        if (CommonUtils.isNotEmpty(userDTO.getEmail())){
            if (CommonUtils.isNotEmpty(hkUserService.byEmail(userDTO.getEmail(), userDTO.getDeptId()))){
                return R.failed("郵箱已存在");
            }
        }
        userDTO.setPassword(DESEncryption.getEncryptString(userDTO.getPassword()));
        userMapper.insertOne(userDTO, "User4399", 1);
        return R.ok("註冊成功");
    }

    @Override
    public void bindingEmailByUserId(Integer userId, String email) {
        userMapper.bindingEmailByUserId(userId, email);
    }

    @Override
    public Integer getDeptIdByUserId(Integer userId) {
        return userMapper.getDeptIdByUserId(userId);
    }

    @Override
    public User getOne(Integer deptId, String email) {
        return userMapper.getOne(deptId, email);
    }

    @Override
    public R checkPw(String password) {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        String pwd = userMapper.getPassword(currentUserId);
        String enPassword = DESEncryption.getEncryptString(password);
        if(enPassword.equals(pwd)){
            return R.ok("密碼一致");
        }else {
            return R.failed("密碼不一致");
        }
    }

    @Override
    public R sendSms() {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        Integer deptId = userMapper.getDeptIdByUserId(currentUserId);
        String phone = userMapper.getPhone(currentUserId);
        String phonePrefix = userMapper.getPre(currentUserId);
            //生成随机验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.CHANGE_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage(phonePrefix, phone, params);
            return R.ok("成功發送短信");
    }

    @Override
    public R checkCode(String code) {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        Integer deptId = userMapper.getDeptIdByUserId(currentUserId);
        String phone = userMapper.getPhone(currentUserId);
        String phonePrefix = userMapper.getPre(currentUserId);
        //判斷redis中的驗證碼是否正確String key = CommonConstants.LOGIN_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone;
        if (code.equals(redisUtils.get(CommonConstants.CHANGE_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone))) {
            return R.ok("驗證碼正確");
        }else {
            return R.failed("驗證碼錯誤");
        }
    }

    @Override
    public R newPhone(String phone,String phonePrefix) {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        Integer deptId = userMapper.getDeptIdByUserId(currentUserId);
        if (CommonUtils.isNotEmpty(hkUserService.byPhone(phonePrefix, phone, deptId))){
            return R.failed("手機號已存在");
        }else {
            //生成随机验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.NEWPHONE_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage(phonePrefix, phone, params);
            return R.ok("成功發送短信");
        }
    }

    @Override
    public R checkCodeByNewPhone(String code,String phone,String phonePrefix) {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        Integer deptId = userMapper.getDeptIdByUserId(currentUserId);
        //判斷redis中的驗證碼是否正確String key = CommonConstants.LOGIN_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone;
        if (code.equals(redisUtils.get(CommonConstants.NEWPHONE_KEY_BY_PHONE + "_" + deptId  + "_+" + phonePrefix + "_" +  phone))) {
            userMapper.changePhone(phone,phonePrefix,currentUserId);
            return R.ok("綁定手機號修改成功");
        }else {
            return R.failed("驗證碼錯誤");
        }
    }

    @Override
    public R checkToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        /** 特殊Token格式判斷 */
        if (token.startsWith(CommonConstants.LOGIN_EMPLOYEES_PREFIX) || token.startsWith(CommonConstants.LOGIN_MANAGER_PREFIX)){
            /** 判斷token的有效性 */
            Object re = redisUtils.get(token);
            if (CommonUtils.isNotEmpty(re)){
            }else {
               return R.failed(401);
            }
        }

        /** token信息提取，格式校验 */
        List<String> audience = new ArrayList<>();
        Object user = null;
        try {
            /** {email, phone, authType, id, deptId, phonePrefix} */
            audience = JWT.decode(token).getAudience();
            //手机号不能为空
            if (audience.get(1) == null) {
                return R.failed(401);
            }
            //登录类型需要符合规范
            if (!"0".equals(audience.get(2)) && !"1".equals(audience.get(2)) && !"2".equals(audience.get(2))) {
                return R.failed(401);
            }
        } catch (JWTDecodeException j) {
            return R.failed(401);
        }
        if ("0".equals(audience.get(2))) {
            //email+password登入方式
            user = hkUserService.byEmail(audience.get(0), Integer.valueOf(audience.get(4)));
        } else if ("1".equals(audience.get(2))) {
            //phone+password登入方式
            user = hkUserService.byPhone(audience.get(5), audience.get(1), Integer.valueOf(audience.get(4)));
        } else if ("2".equals(audience.get(2))) {
            //phone+code登入方式
            user = hkUserService.byPhone(audience.get(5), audience.get(1), Integer.valueOf(audience.get(4)));
            if (CommonUtils.isNotEmpty(user)) {
                return R.ok(200);
            }
        } else {
            return R.failed(401);
        }
        if (user == null) {
            return R.failed(401);
        }
        /** token信息提取，格式校验 */


        /**** 验证token，密码正确性 *****/
        String password = ((HkUser) user).getPassword();
        JWTVerifier jwtVerifier =
                JWT.require(Algorithm.HMAC256(password)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            // 1.token過期
            // 2.密碼錯誤
            // 过滤该请求，不对其进行路由
            return R.failed(401);
        }
        /**** 验证token，密码正确性 *****/
        return R.ok(200);
    }
}
