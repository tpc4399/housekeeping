package com.housekeeping.admin.service;

/**
 * @Author su
 * @create 2021/5/25 12:39
 */
public interface ISerialNumberService {

    /* 获取订单流水号，需要订单number */
    String generateSerialNumber(Long number);

}
