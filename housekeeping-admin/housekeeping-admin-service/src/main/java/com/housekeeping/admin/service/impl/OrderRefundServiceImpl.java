package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.OrderRefundDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.OrderRefundMapper;
import com.housekeeping.admin.mapper.SysJobNoteMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.AgreeRefundByFinance;
import com.housekeeping.admin.vo.OrderRefundVo;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("orderRefundService")
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements IOrderRefundService {

    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private IGroupEmployeesService groupEmployeesService;
    @Override
    public R requireRefund(OrderRefundDTO orderRefundDTO) {

        Long number = orderRefundDTO.getNumber();
        QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
        qw.eq("number",number);
        int count = this.count(qw);
        if(count > 0){
            return R.failed("该订单已提交退款申请");
        }

        CustomerDetails customerDetails = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        OrderRefund orderRefund = new OrderRefund();
        orderRefund.setCustomerId(customerDetails.getId());
        orderRefund.setNumber(orderRefundDTO.getNumber());
        orderRefund.setRequirePrice(orderRefundDTO.getRequirePrice());
        orderRefund.setReason(orderRefundDTO.getReason());
        orderRefund.setVoucher(orderRefundDTO.getVoucher());
        this.save(orderRefund);
        return R.ok("申請退款成功!");
    }

    @Override
    public R getAllRefund() {
        CustomerDetails customerDetails = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
        qw.eq("customer_id",customerDetails.getId());
        List<OrderRefund> list = this.list(qw);
        return R.ok(list);
    }

    @Override
    public R getRefundById(Integer id) {
        OrderRefund byId = this.getById(id);
        return R.ok(byId);
    }

    @Override
    public R getRefundByCom() {
        Integer companyId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",companyId);
        List<Long> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> x.getNumber()).collect(Collectors.toList());

        List<Long> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

        numbers.retainAll(collect);
        List<OrderRefundVo> collect1 = numbers.stream().map(x -> {
            OrderRefund orderRefund = this.getByNumber(x);
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.toString());
            CustomerDetails byId = customerDetailsService.getById(orderRefund.getCustomerId());
            return new OrderRefundVo(orderRefund.getId(), orderRefund.getCustomerId(), orderRefund.getNumber(), orderRefund.getRequirePrice(),
                    orderRefund.getReason(), orderRefund.getVoucher(), orderRefund.getCollectionAccount(),orderRefund.getCompanyAgree(), orderRefund.getCompanyPrice(), orderRefund.getCompanyReason(),
                    orderRefund.getCompanyVoucher(), orderRefund.getFinancePrice(), orderRefund.getFinanceVoucher(), orderRefund.getStatus(), orderDetailsPOJO,byId);
        }).collect(Collectors.toList());
        return R.ok(collect1);
    }



    @Override
    public R getRefundByEmp(Integer empId) {
        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employees_id",empId);
        List<Long> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
            return x.getNumber();
        }).collect(Collectors.toList());

        List<Long> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

        numbers.retainAll(collect);
        List<OrderRefundVo> collect1 = numbers.stream().map(x -> {
            OrderRefund orderRefund = this.getByNumber(x);
            CustomerDetails byId = customerDetailsService.getById(orderRefund.getCustomerId());
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.toString());
            return new OrderRefundVo(orderRefund.getId(), orderRefund.getCustomerId(), orderRefund.getNumber(), orderRefund.getRequirePrice(),
                    orderRefund.getReason(), orderRefund.getVoucher(), orderRefund.getCollectionAccount(),orderRefund.getCompanyAgree(), orderRefund.getCompanyPrice(), orderRefund.getCompanyReason(),
                    orderRefund.getCompanyVoucher(), orderRefund.getFinancePrice(), orderRefund.getFinanceVoucher(), orderRefund.getStatus(), orderDetailsPOJO,byId);
        }).collect(Collectors.toList());
        return R.ok(collect1);

    }

    @Override
    public R getRefundByMan(Integer manId) {
        /* 獲取經理旗下保潔員的Ids */
        List<Integer> empIds = groupEmployeesService.getEmployeesIdsByManager();

        List<Long> numbers = new ArrayList<>();
        for (int i = 0; i < empIds.size(); i++) {
            List<Long> number = new ArrayList<>();
            QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("employees_id",empIds.get(i));
            for (OrderDetails x : orderDetailsService.list(queryWrapper)) {
                Long orderNumber = x.getNumber();
                number.add(orderNumber);
            }
            numbers.addAll(number);
        }

        List<Long> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

        numbers.retainAll(collect);
        List<OrderRefundVo> collect1 = numbers.stream().map(x -> {
            OrderRefund orderRefund = this.getByNumber(x);
            CustomerDetails byId = customerDetailsService.getById(orderRefund.getCustomerId());
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.toString());
            return new OrderRefundVo(orderRefund.getId(), orderRefund.getCustomerId(), orderRefund.getNumber(), orderRefund.getRequirePrice(),
                    orderRefund.getReason(), orderRefund.getVoucher(), orderRefund.getCollectionAccount(),orderRefund.getCompanyAgree(), orderRefund.getCompanyPrice(), orderRefund.getCompanyReason(),
                    orderRefund.getCompanyVoucher(), orderRefund.getFinancePrice(), orderRefund.getFinanceVoucher(), orderRefund.getStatus(), orderDetailsPOJO,byId);
        }).collect(Collectors.toList());
        return R.ok(collect1);

    }

    @Override
    public R refuseRefund(Integer id) {
        OrderRefund byId = this.getById(id);
        byId.setCompanyAgree(false);
        byId.setStatus(1);
        this.updateById(byId);
        return R.ok("已拒絕");
    }

    @Override
    public R agreeRefund(AgreeRefund agreeRefund) {
        OrderRefund byId = this.getById(agreeRefund.getId());
        byId.setCompanyPrice(agreeRefund.getCompanyPrice());
        byId.setCompanyReason(agreeRefund.getCompanyReason());
        byId.setCompanyVoucher(agreeRefund.getCompanyVoucher());
        byId.setCompanyAgree(true);
        this.updateById(byId);
        return R.ok("已同意");
    }

    @Override
    public R getAllAgreeRefund() {
        QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
        qw.eq("company_agree",true);
        List<OrderRefundVo> collect = this.list().stream().map(x -> {
            OrderRefund orderRefund = this.getByNumber(x.getNumber());
            CustomerDetails byId = customerDetailsService.getById(orderRefund.getCustomerId());
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.toString());
            return new OrderRefundVo(orderRefund.getId(), orderRefund.getCustomerId(), orderRefund.getNumber(), orderRefund.getRequirePrice(),
                    orderRefund.getReason(), orderRefund.getVoucher(), orderRefund.getCollectionAccount(), orderRefund.getCompanyAgree(), orderRefund.getCompanyPrice(), orderRefund.getCompanyReason(),
                    orderRefund.getCompanyVoucher(), orderRefund.getFinancePrice(), orderRefund.getFinanceVoucher(), orderRefund.getStatus(), orderDetailsPOJO, byId);
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public R refuseRefundByFinance(Integer id) {
        OrderRefund byId = this.getById(id);
        byId.setStatus(1);
        this.updateById(byId);
        return R.ok("已拒絕");
    }

    @Override
    public R agreeRefundByFinance(AgreeRefundByFinance agreeRefund) {
        //更新退款信息
        OrderRefund byId = this.getById(agreeRefund.getId());
        byId.setFinancePrice(agreeRefund.getCompanyPrice());
        byId.setFinanceVoucher(agreeRefund.getCompanyVoucher());
        byId.setStatus(2);
        this.updateById(byId);

        //更新订单信息
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", byId.getNumber());
        OrderDetails od = orderDetailsService.getOne(qw);
        if (CommonUtils.isEmpty(od)) return R.failed(null, "订单不存在");
        od.setOrderState(CommonConstants.ORDER_STATE_COMPLETED);
        orderDetailsService.updateById(od);
        return R.ok("已同意");
    }

    public OrderRefund getByNumber(Long number){
        QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
        qw.eq("number",number);
        return this.getOne(qw);
    }
}
