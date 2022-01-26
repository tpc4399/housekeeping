package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.OrderRefundDTO;
import com.housekeeping.admin.entity.AgreeRefund;
import com.housekeeping.admin.entity.OrderRefund;
import com.housekeeping.admin.vo.AgreeRefundByFinance;
import com.housekeeping.admin.vo.OrderRefundVo;
import com.housekeeping.common.utils.R;

import java.util.List;


public interface IOrderRefundService extends IService<OrderRefund> {

    R requireRefund(OrderRefundDTO orderRefundDTO);

    List<OrderRefundVo> getAllRefund();

    R getRefundById(Integer id);

    List<OrderRefundVo> getRefundByCom();

    List<OrderRefundVo> getRefundByEmp(Integer empId);

    List<OrderRefundVo> getRefundByMan(Integer manId);

    R refuseRefund(AgreeRefund agreeRefund);

    R agreeRefund(AgreeRefund agreeRefund);

    R getAllAgreeRefund();

    R refuseRefundByFinance(Integer id);

    R agreeRefundByFinance(AgreeRefundByFinance agreeRefund);

    OrderRefund getByNumber(String number);

    R cancelRefund(Integer id);

    List<OrderRefundVo> getByUserId(Integer userId);
}
