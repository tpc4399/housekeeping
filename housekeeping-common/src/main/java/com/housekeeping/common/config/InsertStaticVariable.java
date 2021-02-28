package com.housekeeping.common.config;

import com.housekeeping.common.utils.RedisUtils;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用於注入static變量
 * @Author su
 * @Date 2020/12/2 15:16
 */
@Component
public class InsertStaticVariable implements ApplicationRunner {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TokenUtils.setRedisUtils(redisUtils);
    }
}
