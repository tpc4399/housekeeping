package com.housekeeping.common.config;

import com.housekeeping.common.utils.RedisUtils;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author su
 * @Date 2020/12/2 15:16
 */
@Component
public class InsertStaticVariable implements ApplicationRunner {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //springboot启动完成后执行
        TokenUtils.setRedisUtils(redisUtils);
    }
}
