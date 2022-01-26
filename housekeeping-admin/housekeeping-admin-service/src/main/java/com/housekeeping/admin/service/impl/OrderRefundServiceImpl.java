package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.OrderRefundDTO;
import com.housekeeping.admin.entity.AgreeRefund;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.OrderRefund;
import com.housekeeping.admin.mapper.OrderRefundMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.AgreeRefundByFinance;
import com.housekeeping.admin.vo.OrderRefundVo;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    @Resource
    private IUserService userService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;

    @Override
    public R requireRefund(OrderRefundDTO orderRefundDTO) {

        String number = orderRefundDTO.getNumber();
        QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
        qw.eq("number",number);
        qw.orderByDesc("id");
        List<OrderRefund> list = this.list(qw);
        if(CollectionUtils.isNotEmpty(list)){
            if(list.get(0).getStatus().equals(0)){
                return R.failed("退款申請中，請勿重複提交");
            }
            if(list.get(0).getStatus().equals(2)){
                return R.failed("退款已完成，請勿重複提交");
            }
        }

        CustomerDetails customerDetails = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        OrderRefund orderRefund = new OrderRefund();
        orderRefund.setCollectionAccount(orderRefundDTO.getCollectionAccount());
        orderRefund.setBank(orderRefundDTO.getBank());
        orderRefund.setCustomerId(customerDetails.getId());
        orderRefund.setNumber(orderRefundDTO.getNumber());
        orderRefund.setRequirePrice(orderRefundDTO.getRequirePrice());
        orderRefund.setReason(orderRefundDTO.getReason());
        orderRefund.setVoucher(orderRefundDTO.getVoucher());
        orderRefund.setCreateTime(LocalDateTime.now());
        this.save(orderRefund);
        return R.ok("申請退款成功!");
    }

    @Override
    public List<OrderRefundVo> getAllRefund() {
        CustomerDetails customerDetails = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
        qw.eq("customer_id",customerDetails.getId());
        qw.orderByDesc("id");
        List<OrderRefundVo> collect = this.list(qw).stream().map(x -> {
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
            CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
            return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                    x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                    x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public R getRefundById(Integer id) {
        OrderRefund byId = this.getById(id);
        OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(byId.getNumber().toString());
        CustomerDetails customerDetails = customerDetailsService.getById(byId.getCustomerId());
        OrderRefundVo orderRefundVo = new OrderRefundVo(byId.getId(), byId.getCustomerId(), byId.getNumber(), byId.getRequirePrice(),
                byId.getReason(), byId.getVoucher(), byId.getCollectionAccount(), byId.getBank(), byId.getCompanyAgree(), byId.getCompanyPrice(), byId.getCompanyReason(),
                byId.getCompanyVoucher(), byId.getFinancePrice(), byId.getFinanceVoucher(), byId.getStatus(),byId.getCreateTime(),byId.getCompanyTime(),byId.getFinanceTime(), orderDetailsPOJO, customerDetails);
        return R.ok(orderRefundVo);
    }

    @Override
    public List<OrderRefundVo> getRefundByCom() {
        Integer companyId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",companyId);
        List<String> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> x.getNumber().toString()).collect(Collectors.toList());

        List<String> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

        numbers.retainAll(collect);
        List<OrderRefund> orderRefunds = new ArrayList<>();
        numbers.forEach(x ->{
            QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
            qw.eq("number",x);
            List<OrderRefund> list = this.list(qw);
            orderRefunds.addAll(list);
        });
        List<OrderRefund> collect2 = orderRefunds.stream().sorted(Comparator.comparing(OrderRefund::getId).reversed()).collect(Collectors.toList());
        List<OrderRefundVo> collect1 = collect2.stream().map(x -> {
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
            CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
            return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                    x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                    x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
        }).collect(Collectors.toList());

        return collect1;
    }



    @Override
    public List<OrderRefundVo> getRefundByEmp(Integer empId) {
        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employees_id",empId);
        List<String> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
            return x.getNumber().toString();
        }).collect(Collectors.toList());

        List<String> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

        numbers.retainAll(collect);
        List<OrderRefund> orderRefunds = new ArrayList<>();
        numbers.forEach(x ->{
            QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
            qw.eq("number",x);
            List<OrderRefund> list = this.list(qw);
            orderRefunds.addAll(list);
        });
        List<OrderRefund> collect2 = orderRefunds.stream().sorted(Comparator.comparing(OrderRefund::getId).reversed()).collect(Collectors.toList());
        List<OrderRefundVo> collect1 = collect2.stream().map(x -> {
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
            CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
            return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                    x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                    x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
        }).collect(Collectors.toList());

        return collect1;

    }

    @Override
    public List<OrderRefundVo> getByUserId(Integer userId) {
        Integer deptId = userService.getById(userId).getDeptId();
        if(deptId.equals(2)){
            Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
            QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_id",companyId);
            List<String> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> x.getNumber().toString()).collect(Collectors.toList());

            List<String> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

            numbers.retainAll(collect);
            List<OrderRefund> orderRefunds = new ArrayList<>();
            numbers.forEach(x ->{
                QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
                qw.eq("number",x);
                List<OrderRefund> list = this.list(qw);
                orderRefunds.addAll(list);
            });
            List<OrderRefund> collect2 = orderRefunds.stream().sorted(Comparator.comparing(OrderRefund::getId).reversed()).collect(Collectors.toList());
            List<OrderRefundVo> collect1 = collect2.stream().map(x -> {
                OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
                CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
                return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                        x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                        x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
            }).collect(Collectors.toList());
            return collect1;
        }
        if(deptId.equals(3)){
            CustomerDetails customerDetails = customerDetailsService.getByUserId(userId);
            QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
            qw.eq("customer_id",customerDetails.getId());
            qw.orderByDesc("id");
            List<OrderRefundVo> collect = this.list(qw).stream().map(x -> {
                OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
                CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
                return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                        x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                        x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
            }).collect(Collectors.toList());

            return collect;
        }
        if(deptId.equals(4)){
            Integer id = managerDetailsService.getManagerDetailsByUserId(userId).getId();
            /* 獲取經理旗下保潔員的Ids */
            List<Integer> empIds = groupEmployeesService.getEmployeesIdsByManagerId(id);

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

            List<String> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

            numbers.retainAll(collect);
            List<OrderRefund> orderRefunds = new ArrayList<>();
            numbers.forEach(x ->{
                QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
                qw.eq("number",x);
                List<OrderRefund> list = this.list(qw);
                orderRefunds.addAll(list);
            });
            List<OrderRefund> collect2 = orderRefunds.stream().sorted(Comparator.comparing(OrderRefund::getId).reversed()).collect(Collectors.toList());
            List<OrderRefundVo> collect1 = collect2.stream().map(x -> {
                OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
                CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
                return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                        x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                        x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
            }).collect(Collectors.toList());

            return collect1;
        }
        if(deptId.equals(5)){
            Integer empId = employeesDetailsService.getEmployeesIdByUserId(userId);
            QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("employees_id",empId);
            List<String> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
                return x.getNumber().toString();
            }).collect(Collectors.toList());

            List<String> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

            numbers.retainAll(collect);
            List<OrderRefund> orderRefunds = new ArrayList<>();
            numbers.forEach(x ->{
                QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
                qw.eq("number",x);
                List<OrderRefund> list = this.list(qw);
                orderRefunds.addAll(list);
            });
            List<OrderRefund> collect2 = orderRefunds.stream().sorted(Comparator.comparing(OrderRefund::getId).reversed()).collect(Collectors.toList());
            List<OrderRefundVo> collect1 = collect2.stream().map(x -> {
                OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
                CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
                return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                        x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                        x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
            }).collect(Collectors.toList());

            return collect1;

        }
        if(deptId.equals(6)){
            Integer empId = employeesDetailsService.getEmployeesIdByUserId(userId);
            QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("employees_id",empId);
            List<String> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
                return x.getNumber().toString();
            }).collect(Collectors.toList());

            List<String> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

            numbers.retainAll(collect);
            List<OrderRefund> orderRefunds = new ArrayList<>();
            numbers.forEach(x ->{
                QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
                qw.eq("number",x);
                List<OrderRefund> list = this.list(qw);
                orderRefunds.addAll(list);
            });
            List<OrderRefund> collect2 = orderRefunds.stream().sorted(Comparator.comparing(OrderRefund::getId).reversed()).collect(Collectors.toList());
            List<OrderRefundVo> collect1 = collect2.stream().map(x -> {
                OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
                CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
                return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                        x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                        x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
            }).collect(Collectors.toList());

            return collect1;
        }
        return null;
    }

    @Override
    public List<OrderRefundVo> getRefundByMan(Integer manId) {
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

        List<String> collect = this.list().stream().map(x -> x.getNumber()).collect(Collectors.toList());

        numbers.retainAll(collect);
        List<OrderRefund> orderRefunds = new ArrayList<>();
        numbers.forEach(x ->{
            QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
            qw.eq("number",x);
            List<OrderRefund> list = this.list(qw);
            orderRefunds.addAll(list);
        });
        List<OrderRefund> collect2 = orderRefunds.stream().sorted(Comparator.comparing(OrderRefund::getId).reversed()).collect(Collectors.toList());
        List<OrderRefundVo> collect1 = collect2.stream().map(x -> {
            OrderDetailsPOJO orderDetailsPOJO = orderDetailsService.getByNumber(x.getNumber());
            CustomerDetails byId = customerDetailsService.getById(x.getCustomerId());
            return new OrderRefundVo(x.getId(), x.getCustomerId(), x.getNumber(), x.getRequirePrice(),
                    x.getReason(), x.getVoucher(), x.getCollectionAccount(),x.getBank(), x.getCompanyAgree(), x.getCompanyPrice(), x.getCompanyReason(),
                    x.getCompanyVoucher(), x.getFinancePrice(), x.getFinanceVoucher(), x.getStatus(),x.getCreateTime(),x.getCompanyTime(),x.getFinanceTime(), orderDetailsPOJO, byId);
        }).collect(Collectors.toList());

        return collect1;
    }

    @Override
    public R refuseRefund(AgreeRefund agreeRefund) {
        OrderRefund byId = this.getById(agreeRefund.getId());
        byId.setCompanyTime(LocalDateTime.now());
        byId.setCompanyAgree(false);
        byId.setCompanyPrice(agreeRefund.getCompanyPrice());
        byId.setCompanyReason(agreeRefund.getCompanyReason());
        byId.setCompanyVoucher(agreeRefund.getCompanyVoucher());
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
        byId.setCompanyTime(LocalDateTime.now());
        byId.setStatus(3);
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
                    orderRefund.getReason(), orderRefund.getVoucher(), orderRefund.getCollectionAccount(), orderRefund.getBank(), orderRefund.getCompanyAgree(), orderRefund.getCompanyPrice(), orderRefund.getCompanyReason(),
                    orderRefund.getCompanyVoucher(), orderRefund.getFinancePrice(), orderRefund.getFinanceVoucher(), orderRefund.getStatus(),orderRefund.getCreateTime(),orderRefund.getCompanyTime(),orderRefund.getFinanceTime(), orderDetailsPOJO, byId);
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public R refuseRefundByFinance(Integer id) {
        OrderRefund byId = this.getById(id);
        byId.setStatus(1);
        byId.setFinanceTime(LocalDateTime.now());
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
        byId.setFinanceTime(LocalDateTime.now());
        this.updateById(byId);

        /*//更新订单信息
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", byId.getNumber());
        OrderDetails od = orderDetailsService.getOne(qw);
        if (CommonUtils.isEmpty(od)) return R.failed(null, "订单不存在");
        od.setOrderState(CommonConstants.ORDER_STATE_COMPLETED);
        orderDetailsService.updateById(od);*/
        return R.ok("已同意");
    }

    public OrderRefund getByNumber(String number){
        QueryWrapper<OrderRefund> qw = new QueryWrapper<>();
        qw.eq("number",number);
        qw.orderByDesc("id");
        List<OrderRefund> list = this.list(qw);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    @Override
    public R cancelRefund(Integer id) {
        OrderRefund byId = this.getById(id);
        if(byId.getStatus().equals(2)){
            return R.failed("該訂單已完成，無法取消");
        }
        byId.setStatus(1);
        this.updateById(byId);
        return R.ok("取消成功");
    }


}
