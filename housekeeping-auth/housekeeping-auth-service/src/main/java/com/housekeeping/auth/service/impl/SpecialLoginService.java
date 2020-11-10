package com.housekeeping.auth.service.impl;

import com.housekeeping.auth.service.ISpecialLoginService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author su
 * @create 2020/11/10 16:39
 */
@Service("specialLoginService")
public class SpecialLoginService implements ISpecialLoginService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public R authEmployees(String key) {
        key = CommonConstants.LOGIN_EMPLOYEES_PREFIX + key;
        Object re =  redisUtils.get(key);
        if (CommonUtils.isNotEmpty(re)){
//            生成token並返回
            Map<String, Object> map = new HashMap();
            map.put("Token", key);
            map.put("EmployeesId", re);
            return R.ok(map, "登入成功");
        }else {
            return R.failed("鏈接失效，請聯繫公司管理員");
        }
    }

    @Override
    public R authManager(String key) {
        key = CommonConstants.LOGIN_MANAGER_PREFIX + key;
        Object re =  redisUtils.get(key);
        if (CommonUtils.isNotEmpty(re)){
//            生成token並返回
            Map<String, Object> map = new HashMap();
            map.put("Token", key);
            map.put("ManagerId", re);
            return R.ok(map, "登入成功");
        }else {
            return R.failed("鏈接失效，請聯繫公司管理員");
        }
    }
}
