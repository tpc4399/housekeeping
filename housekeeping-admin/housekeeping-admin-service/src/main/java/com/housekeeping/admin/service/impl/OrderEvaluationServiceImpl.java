package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.admin.service.IOrderEvaluationService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2021/6/7 9:24
 */
@Service("orderEvaluationService")
public class OrderEvaluationServiceImpl implements IOrderEvaluationService {
    @Override
    public R evaluation(OrderEvaluationDTO dto) {
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_CUSTOMER)){

        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){

        }
        return R.ok();
    }

    @Override
    public void customerEvaluation(OrderEvaluationDTO dto) {
    }

    @Override
    public void employeesEvaluation(OrderEvaluationDTO dto) {
    }

}
