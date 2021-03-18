package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CompanyWorkListMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/18 16:10
 */
@Service("companyWorkListService")
public class CompanyWorkListServiceImpl extends ServiceImpl<CompanyWorkListMapper, CompanyWorkList> implements ICompanyWorkListService {

    @Resource
    private IGroupEmployeesService groupEmployeesService;
    @Resource
    private ISysOrderPlanService sysOrderPlanService;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesWorksheetPlanService employeesWorksheetPlanService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private ICustomerDemandPlanService customerDemandPlanService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private IDemandOrderService demandOrderService;

    @Override
    public R beInterested(Integer demandOrderId) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(qw);
        Integer companyId = companyDetails.getId();
        LocalDateTime now = LocalDateTime.now();
        CompanyWorkList companyWorkList = new CompanyWorkList(null, companyId, demandOrderId, now, false, now);
        this.save(companyWorkList);
        return R.ok(null, "成功添加到興趣列表");
    }

    @Override
    public R suitableEmployees(Integer demandOrderId) {
        return null;
    }

    @Override
    public R selectSuitableEmployees(String employeesId, Integer demandOrderId) {
        return null;
    }

    @Override
    public R initiateChat(String demandOrderId) {
        /* 先检查保洁员、公司、客户全不全，完不完整 */

        return null;
    }

    @Override
    public R requestToSendTemporaryOrder(Integer demandOrderId, Integer companyId) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        CustomerDetails customerDetails = customerDetailsService.getOne(qw);
        Integer customerId = customerDetails.getId();
        DemandOrder demandOrder = demandOrderService.getById(demandOrderId);
        if (demandOrder.getCustomerId().equals(customerId)){
            return R.failed(null ,"這不是你的訂單~");
        }
        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("demand_order_id", demandOrderId);
        qw2.eq("company_id", companyId);
        CompanyWorkList companyWorkList = this.getOne(qw2);
        companyWorkList.setTemporaryOrderRequest(true);
        companyWorkList.setRequestTime(LocalDateTime.now());
        this.updateById(companyWorkList);
        /* 获取临时订单数据 */

        return R.ok(null, "請求成功");
    }
}
