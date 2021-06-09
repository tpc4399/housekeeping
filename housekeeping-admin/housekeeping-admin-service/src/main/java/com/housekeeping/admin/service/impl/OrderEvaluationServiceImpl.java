package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.admin.entity.OrderEvaluation;
import com.housekeeping.admin.mapper.OrderEvaluationMapper;
import com.housekeeping.admin.service.IOrderEvaluationService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author su
 * @create 2021/6/7 9:24
 */
@Service("orderEvaluationService")
public class OrderEvaluationServiceImpl implements IOrderEvaluationService {

    @Resource
    private OrderEvaluationMapper orderEvaluationMapper;

    @Override
    public R evaluation(OrderEvaluationDTO dto) {
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_CUSTOMER)) customerEvaluation(dto);
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)) employeesEvaluation(dto);
        return R.ok(null, "評價成功");
    }

    @Override
    public void customerEvaluation(OrderEvaluationDTO dto) {
        orderEvaluationMapper.customerEvaluation(dto);
    }

    @Override
    public void employeesEvaluation(OrderEvaluationDTO dto) {
        orderEvaluationMapper.employeesEvaluation(dto);
    }

    @Override
    public R getEvaluation(String orderNumber) {
        OrderEvaluation oe = orderEvaluationMapper.getEvaluation(orderNumber);
        if (oe == null) return R.ok(null, "訂單號不存在");
        if (oe.getYes1() == false && oe.getYes2()) return R.ok(oe, "客戶未評價");
        if (oe.getYes1() && oe.getYes2() == false) return R.ok(oe, "保潔員未評價");
        if (oe.getYes1() == false && oe.getYes2() == false) return R.ok(oe, "保潔員與客戶都沒評價");

        return R.ok(oe, "保潔員與客戶都已經評價了");
    }

}
