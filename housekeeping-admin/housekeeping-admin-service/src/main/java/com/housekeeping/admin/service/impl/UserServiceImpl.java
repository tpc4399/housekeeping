package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.ForgetDTO;
import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.UserMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ICompanyDetailsService companyService;

    @Resource
    private ICustomerDetailsService customerDetailsService;

    @Override
    public User getUserByPhone(String phonePrefix, String phone, Integer deptId) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone_prefix", phonePrefix);
        qr.eq("dept_id", deptId);
        qr.eq("phone", phone);
        User res = baseMapper.selectOne(qr);

        return res;
    }

    @Override
    public R checkData(Integer deptId, String data, Integer type) {
        QueryWrapper qr = new QueryWrapper();
        switch (type) {
            case 1:
                qr.eq("dept_id", deptId);
                qr.eq("phone", data);
                break;
            case 2:
                qr.eq("dept_id", deptId);
                qr.eq("email", data);
                break;
        }
        if (this.userMapper.selectCount(qr) == 0) {
            return R.ok("手机号或者邮箱不存在,可以注册");
        } else {
            return R.failed("手机号或者邮箱已存在");
        }
    }

    @Override
    public R sendRegisterMSMessage(String phonePrefix, String phone, Integer deptId) {
        User hkUser = this.getUserByPhone(phonePrefix, phone, deptId);
        if (CommonUtils.isEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.REGISTER_KEY_BY_PHONE + "_" + deptId + "_+" + phonePrefix + "_" + phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage(phonePrefix, phone, params);
            return R.ok("成功發送短信");
        } else {
            return R.failed("該手機號已註冊");
        }
    }

    @Override
    @Transactional
    public R saveEmp(RegisterDTO registerDTO) {
        if (CommonUtils.isNotEmpty(registerDTO)) {
            if (CommonUtils.isNotEmpty(registerDTO.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (registerDTO.getCode().equals(redisUtils.get(CommonConstants.REGISTER_KEY_BY_PHONE + "_" + 2 + "_+" + registerDTO.getPhonePrefix() + "_" + registerDTO.getPhone()))) {
                    if (registerDTO.getPassword().equals(registerDTO.getRepassword())) {
                        //先保存User
                        User user = new User();
                        String s = String.valueOf(System.currentTimeMillis());
                        user.setNumber("c"+s);
                        user.setDeptId(2);
                        user.setName(registerDTO.getName());
                        user.setPhonePrefix(registerDTO.getPhonePrefix());
                        user.setPhone(registerDTO.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(registerDTO.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        Integer maxUserId = 0;
                        synchronized (this) {
                            this.save(user);
                            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
                        }
                        //在保存後台管理員詳情信息
                        CompanyDetails companyDetails = new CompanyDetails();
                        companyDetails.setCompanyName(registerDTO.getName());
                        companyDetails.setUserId(maxUserId);
                        companyDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                        companyDetails.setCreateTime(LocalDateTime.now());
                        companyDetails.setUpdateTime(LocalDateTime.now());
                        companyService.save(companyDetails);
                    } else {
                        return R.failed("两次密码不一致");
                    }
                } else {
                    return R.failed("验证码错误");
                }
            } else {
                return R.failed("验证码为空");
            }
        }
        return R.ok("创建公司账户成功");
    }

    @Override
    public R saveCus(RegisterDTO registerDTO) {
        if (CommonUtils.isNotEmpty(registerDTO)) {
            if (CommonUtils.isNotEmpty(registerDTO.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (registerDTO.getCode().equals(redisUtils.get(CommonConstants.REGISTER_KEY_BY_PHONE + "_" + 3 + "_+" + registerDTO.getPhonePrefix() + "_" + registerDTO.getPhone()))) {
                    if (registerDTO.getPassword().equals(registerDTO.getRepassword())) {
                        User user = new User();
                        user.setNumber(String.valueOf(System.currentTimeMillis()));
                        user.setDeptId(3);
                        user.setName(registerDTO.getName());
                        user.setPhonePrefix(registerDTO.getPhonePrefix());
                        user.setPhone(registerDTO.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(registerDTO.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        Integer maxUserId = 0;
                        synchronized (this) {
                            this.save(user);
                            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
                        }
                        //在保存後台管理員詳情信息
                        CustomerDetails customerDetails = new CustomerDetails();
                        customerDetails.setPhonePrefix(registerDTO.getPhonePrefix());
                        customerDetails.setPhone(registerDTO.getPhone());
                        customerDetails.setUserId(maxUserId);
                        customerDetails.setBlacklistFlag(false);//默认值false
                        customerDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                        customerDetails.setCreateTime(LocalDateTime.now());
                        customerDetails.setUpdateTime(LocalDateTime.now());
                        customerDetailsService.save(customerDetails);
                    } else {
                        return R.ok("两次密码不一致");
                    }
                } else {
                    return R.failed("验证码错误");
                }
            } else {
                return R.failed("验证码为空");
            }
        }
        return R.ok("创建客户成功");
    }

    @Override
    public R saveAdmin(RegisterDTO registerDTO) {
        if (CommonUtils.isNotEmpty(registerDTO)) {
            if (CommonUtils.isNotEmpty(registerDTO.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (registerDTO.getCode().equals(redisUtils.get(CommonConstants.REGISTER_KEY_BY_PHONE + "_" + 1 + "_+" + registerDTO.getPhonePrefix() + "_" + registerDTO.getPhone()))) {
                    if (registerDTO.getPassword().equals(registerDTO.getRepassword())) {
                        User user = new User();
                        user.setNumber(String.valueOf(System.currentTimeMillis()));
                        user.setDeptId(1);
                        user.setName(registerDTO.getName());
                        user.setPhonePrefix(registerDTO.getPhonePrefix());
                        user.setPhone(registerDTO.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(registerDTO.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        Integer maxUserId = 0;
                        synchronized (this) {
                            this.save(user);
                            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
                        }
                        //再保存後台管理員詳情信息

                    } else {
                        return R.failed("两次密码不一致");
                    }
                } else {
                    return R.failed("验证码错误");
                }
            } else {
                return R.failed("验证码为空");
            }
        }
        return R.ok("创建平台管理员成功");
    }

    @Override
    public R sendForgetMSMessage(String phonePrefix, String phone, Integer deptId) {
        User hkUser = this.getUserByPhone(phonePrefix, phone, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.FORGET_KEY_BY_PHONE + "_" + deptId + "_+" + phonePrefix + "_" + phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage(phonePrefix, phone, params);
            return R.ok("成功發送短信");
        } else {
            return R.failed("該手機號未註冊");
        }
    }

    @Override
    public R updatePwd(ForgetDTO forgetDTO) {
        if (CommonUtils.isNotEmpty(forgetDTO)) {
            if (CommonUtils.isNotEmpty(forgetDTO.getCode())) {
                if (forgetDTO.getCode().equals(redisUtils.get(CommonConstants.FORGET_KEY_BY_PHONE + "_" + forgetDTO.getDeptId() + "_+" + forgetDTO.getPhonePrefix() + "_" + forgetDTO.getPhone()))) {
                    if(forgetDTO.getPassword().equals(forgetDTO.getRePassword())){
                        User user = this.getUserByPhone(forgetDTO.getPhonePrefix(), forgetDTO.getPhone(), forgetDTO.getDeptId());
                        user.setPassword(DESEncryption.getEncryptString(forgetDTO.getPassword()));
                        this.updateById(user);
                    }else {
                        return R.failed("兩次密碼不一致");
                    }
                } else {
                    return R.failed("驗證碼錯誤");
                }
            } else {
                return R.failed("驗證碼爲空");
            }
        }
        return R.ok("密碼修改成功");
    }

    @Override
    public R verfifyCode(String phonePrefix, String phone, String code,Integer deptId) {
        if (code.equals(redisUtils.get(CommonConstants.FORGET_KEY_BY_PHONE + "_" + deptId + "_+" + phonePrefix + "_" + phone))){
            return R.ok("验证码通过");
        }
        return R.failed("验证码错误");
    }

}
