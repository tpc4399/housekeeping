package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.OrderRefundDTO;
import com.housekeeping.admin.entity.AgreeRefund;
import com.housekeeping.admin.entity.OrderRefund;
import com.housekeeping.admin.entity.SysJobNote;
import com.housekeeping.admin.vo.AgreeRefundByFinance;
import com.housekeeping.common.utils.R;

import java.util.List;


public interface IOrderRefundService extends IService<OrderRefund> {

    R requireRefund(OrderRefundDTO orderRefundDTO);

    R getAllRefund();

    R getRefundById(Integer id);

    R getRefundByCom();

    R getRefundByEmp(Integer empId);

    R getRefundByMan(Integer manId);

    R refuseRefund(Integer id);

    R agreeRefund(AgreeRefund agreeRefund);

    R getAllAgreeRefund();

    R refuseRefundByFinance(Integer id);

    R agreeRefundByFinance(AgreeRefundByFinance agreeRefund);
}
