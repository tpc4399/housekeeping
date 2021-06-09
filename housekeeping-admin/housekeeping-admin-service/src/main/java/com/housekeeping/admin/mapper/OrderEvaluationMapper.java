package com.housekeeping.admin.mapper;

import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.admin.entity.OrderEvaluation;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * @Author su
 * @create 2021/6/7 9:23
 */
public interface OrderEvaluationMapper {
    void customerEvaluation(@Param("dto") OrderEvaluationDTO dto);//客户评价订单
    void employeesEvaluation(@Param("dto") OrderEvaluationDTO dto);//保洁员评价订单
    OrderEvaluation getEvaluation(String orderNumber);
}
