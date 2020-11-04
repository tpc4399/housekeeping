package com.housekeeping.auth.service.impl;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.auth.mapper.UserMapper;
import com.housekeeping.auth.service.IUserService;
import com.housekeeping.common.utils.DESEncryption;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
