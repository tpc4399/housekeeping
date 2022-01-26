package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.OrderEvaluation;
import com.housekeeping.admin.mapper.OrderEvaluationMapper;
import com.housekeeping.admin.service.IOrderDetailsService;
import com.housekeeping.admin.service.IOrderEvaluationService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
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
    @Resource
    private IOrderDetailsService orderDetailsService;

    @Override
    public R evaluation(OrderEvaluationDTO dto) {
        //TODO 判断订单所有者
        //执行评价流程
        String roleType = TokenUtils.getRoleType();
        if(roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)) employeesEvaluation(dto);
        if(roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)) employeesEvaluation(dto);
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_CUSTOMER)) customerEvaluation(dto);
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)) employeesEvaluation(dto);
        //检测订单是否已完成
        this.evaluationStatusHandle(dto.getOrderNumber());
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

    @Override
    public Boolean getEvaluationStatusOfEmployees(String orderNumber) {
        OrderEvaluation oe = orderEvaluationMapper.getEvaluation(orderNumber);
        if (oe == null) return false;
        if (oe.getYes2() == false) return false;
        return true;
    }

    @Override
    public Boolean getEvaluationStatusOfCustomer(String orderNumber) {
        OrderEvaluation oe = orderEvaluationMapper.getEvaluation(orderNumber);
        if (oe == null) return false;
        if (oe.getYes1() == false) return false;
        return true;
    }

    @Override
    public void evaluationStatusHandle(String orderNumber) {
        OrderEvaluation oe = orderEvaluationMapper.getEvaluation(orderNumber);
        if (oe.getYes1().equals(true) && oe.getYes2().equals(true)) {
            //修改订单状态
            orderEvaluationMapper.setOrderDetails(orderNumber);
        }
    }

}
