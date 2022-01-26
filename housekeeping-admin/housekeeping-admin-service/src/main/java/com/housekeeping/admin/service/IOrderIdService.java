package com.housekeeping.admin.service;

/**
 * @Author su
 * @Date 2021/4/14 11:51
 */
public interface IOrderIdService {

    /* 生成几百个订单编号到redis */
    void orderIdGenerate(Integer counter);
    long generateId();

}
