package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.InvitationDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.InvitationMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.pojo.OrderDetailsParent;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.PageUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service("invitationService")
public class InvitationServiceImpl extends ServiceImpl<InvitationMapper, Invitation> implements InvitationService {

    @Resource
    private IUserService userService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private IOrderEvaluationService orderEvaluationService;
    @Resource
    private ISysJobContendService sysJobContendService;

    @Override
    public R getAllInvitees(Integer userId) {
        QueryWrapper<Invitation> wrapper = new QueryWrapper<>();
        wrapper.eq("invitee",userId);
        List<Invitation> list = this.list(wrapper);

        List collect =  new ArrayList<InvitationDTO>();
        for (Invitation invitation : list) {
            User user = userService.getById(invitation.getInvitees());
            if(CommonUtils.isNotEmpty(user)){
                InvitationDTO invitationDTO = new InvitationDTO();
                invitationDTO.setId(user.getId());
                invitationDTO.setDeptId(user.getDeptId());
                invitationDTO.setName(user.getName());
                invitationDTO.setPhonePrefix(user.getPhonePrefix());
                invitationDTO.setPhone(user.getPhone());
                invitationDTO.setNickname(user.getNickname());
                String headUrl = this.getHeadUrl(invitation.getInvitees());
                invitationDTO.setHeadUrl(headUrl);
                invitationDTO.setBonus(invitation.getBonus());
                collect.add(invitationDTO);
            }
        }

        User user = userService.getById(userId);

        HashMap map  = new HashMap();
        map.put("invitee",collect);
        map.put("bonus",user.getBonus());
        return R.ok(map);
    }

    @Override
    public R getOrders(Integer userId) {
        QueryWrapper<Invitation> wrapper = new QueryWrapper<>();
        wrapper.eq("invitee",userId);
        List<Invitation> list = this.list(wrapper);
        ArrayList<OrderDetails> orderDetails = new ArrayList<>();
        for (Invitation invitation : list) {
            User byId = userService.getById(invitation.getInvitees());
            if(CommonUtils.isNotEmpty(byId)){
                if(byId.getDeptId().equals(3)){
                    CustomerDetails customerDetails = customerDetailsService.getByUserId(invitation.getInvitees());
                    QueryWrapper<OrderDetails> qw = new QueryWrapper<>();
                    qw.eq("customer_id",customerDetails.getId());
                    qw.in("order_state",15,20);
                    List<OrderDetails> list1 = orderDetailsService.list(qw);
                    orderDetails.addAll(list1);
                }
                if(byId.getDeptId().equals(6)){
                    EmployeesDetails employeesDetails = employeesDetailsService.getByUserId(invitation.getInvitees());
                    QueryWrapper<OrderDetails> qw = new QueryWrapper<>();
                    qw.eq("employees_id",employeesDetails.getId());
                    qw.in("order_state",15,20);
                    List<OrderDetails> list1 = orderDetailsService.list(qw);
                    orderDetails.addAll(list1);
                }
            }
        }
        orderDetails.sort(Comparator.comparing(OrderDetails::getStartDateTime));
        List<OrderDetailsPOJO> res = orderDetails.stream().map(x -> {
            OrderDetailsPOJO orderDetailsPOJO = new OrderDetailsPOJO(x);
            return orderDetailsPOJO;
        }).collect(Collectors.toList());

        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            son.setEmployeesHeadUrl(employeesDetailsService.getById(x.getEmployeesId()).getHeadUrl());
            son.setCustomerHeadUrl(customerDetailsService.getById(x.getCustomerId()).getHeadUrl());
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(x.getNumber());
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(x.getNumber());
            son.setYes1(yes1);
            son.setYes2(yes2);
            return son;
        }).collect(Collectors.toList());
        return R.ok(sons);
    }

    @Override
    public R getAllUser(Page page) {
        List<Integer> ids = baseMapper.getAllInvitee();

        ArrayList<User> users = new ArrayList<>();
        for (Integer id : ids) {
            User byId = userService.getById(id);
            if(CommonUtils.isNotEmpty(byId)){
                users.add(byId);
            }
        }

        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), users);
        return R.ok(pages);
    }


    public String getHeadUrl(Integer userId){
        User byId = userService.getById(userId);
        Integer deptId = byId.getDeptId();
        if(deptId.equals(2)){
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id", userId);
            CompanyDetails companyDetails = companyDetailsService.getOne(queryWrapper);
            return companyDetails.getLogoUrl();
        }
        if(deptId.equals(3)){
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id", userId);
            CustomerDetails customerDetails = customerDetailsService.getOne(queryWrapper);
            return customerDetails.getHeadUrl();
        }
        if(deptId.equals(6)){
            QueryWrapper qw = new QueryWrapper();
            qw.eq("user_id", userId);
            CompanyDetails companyDetails = companyDetailsService.getOne(qw);

            return companyDetails.getLogoUrl();
        }else {
            return null;
        }

    }
}
