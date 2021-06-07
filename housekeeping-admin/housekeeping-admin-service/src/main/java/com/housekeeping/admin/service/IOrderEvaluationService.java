package com.housekeeping.admin.service;

import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @create 2021/6/7 9:24
 */
public interface IOrderEvaluationService {
    R evaluation(@RequestBody OrderEvaluationDTO dto); //评价服务
    void customerEvaluation(OrderEvaluationDTO dto);//客户评价订单
    void employeesEvaluation(OrderEvaluationDTO dto);//保洁员评价订单


}
