package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyWorkList;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/18 16:09
 */
public interface ICompanyWorkListService extends IService<CompanyWorkList> {
    /* 添加需求订单到感兴趣列表 */
    R beInterested(Integer demandOrderId);
    /* 生成每个员工的临时订单，返回筛选后的员工ids */
    R suitableEmployees(Integer demandOrderId);
    /* 存储该保洁员的的临时订单 */
    R selectSuitableEmployees(String employeesId, Integer demandOrderId);
    /* 发起聊天 */
    R initiateChat(String demandOrderId);
    /* 请求发送临时订单 */
    R requestToSendTemporaryOrder(Integer demandOrderId, Integer companyId);

}
