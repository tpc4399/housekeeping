package com.housekeeping.admin.service;

import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2021/6/7 9:24
 */
public interface IOrderEvaluationService {
    R evaluation(OrderEvaluationDTO dto); //评价服务
    void customerEvaluation(OrderEvaluationDTO dto);//客户评价订单
    void employeesEvaluation(OrderEvaluationDTO dto);//保洁员评价订单
    R getEvaluation(String orderNumber);//查看某個訂單評價
    Boolean getEvaluationStatusOfEmployees(String orderNumber);//查看保洁员评价状态
    Boolean getEvaluationStatusOfCustomer(String orderNumber);//查看客户评价状态
    void evaluationStatusHandle(String orderNumber);//查看订单评价状态处理 ，如果已评价，修改订单状态


}
