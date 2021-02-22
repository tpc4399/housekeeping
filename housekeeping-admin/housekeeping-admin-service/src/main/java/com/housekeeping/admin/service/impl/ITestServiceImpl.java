package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.service.ITestService;
import com.housekeeping.admin.vo.EmployeesHandleVo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author su
 * @Date 2021/2/20 10:44
 */
@Service("testService")
public class ITestServiceImpl implements ITestService {
    @Async
    @Override
    public void syncMethod(Integer i) {
        System.out.println(Thread.currentThread().getName() + " " + i);
    }

    @Override
    public void threadMethod() {
//        Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28);
        List<Integer> list = Arrays.asList(1,2,3,4,5);
        ExecutorService ex = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            ex.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+ " Id:" + finalI + "開始");
                    list.forEach(x -> {
                        System.out.println(Thread.currentThread().getName()+ " Id:" + finalI + "-->" + x);
                    });
                    System.out.println(Thread.currentThread().getName()+ " Id:" + finalI + "開始");

                }
            });
        }
        ex.shutdown();
    }
}
