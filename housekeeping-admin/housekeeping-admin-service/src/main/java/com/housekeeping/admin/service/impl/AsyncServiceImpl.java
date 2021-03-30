package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.dto.EmployeesInstanceDTO;
import com.housekeeping.admin.service.IAsyncService;
import com.housekeeping.common.utils.RedisUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author su
 * @Date 2021/3/30 18:45
 */
@Service("asyncService")
public class AsyncServiceImpl implements IAsyncService {

    @Resource
    private RedisUtils redisUtils;

    @Async
    @Override
    public void setRedisDos(List<EmployeesInstanceDTO> dos) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sjabfaga");
    }
}
