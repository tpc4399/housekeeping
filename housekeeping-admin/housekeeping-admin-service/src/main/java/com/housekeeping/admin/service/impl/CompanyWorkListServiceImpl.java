package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.MakeAnAppointmentDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CompanyWorkListMapper;
import com.housekeeping.admin.mapper.DemandEmployeesMapper;
import com.housekeeping.admin.mapper.DemandOrderMapper;
import com.housekeeping.admin.pojo.ConfirmOrderPOJO;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.DemandEmployeesVo;
import com.housekeeping.admin.vo.QuotationVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.omg.CORBA.portable.Delegate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author su
 * @create 2020/11/18 16:10
 */
@Service("companyWorkListService")
public class CompanyWorkListServiceImpl extends ServiceImpl<CompanyWorkListMapper, CompanyWorkList> implements ICompanyWorkListService {

    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private IDemandOrderService demandOrderService;
    @Resource
    private IDemandEmployeesService demandEmployeesService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private DemandEmployeesMapper demandEmployeesMapper;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private IGroupManagerService groupManagerService;
    @Resource
    private IGroupEmployeesService groupEmployeesService;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private DemandOrderMapper demandOrderMapper;
    @Resource
    private IOrderIdService orderIdService;
    /*@Override
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
    }*/

    @Override
    public R suitableEmployees(Integer userId,Integer typeId) {
        List<EmployeesDetails> employeesDetails = new ArrayList<>();
        if(typeId==0){
            QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
            qw.eq("user_id",userId);
            CompanyDetails one = companyDetailsService.getOne(qw);
            if(CommonUtils.isEmpty(one)){
                return R.ok(null);
            }
            QueryWrapper<EmployeesDetails> qw2 = new QueryWrapper<>();
            qw2.eq("company_id",one.getId());
            List<EmployeesDetails> list = employeesDetailsService.list(qw2);
            employeesDetails.addAll(list);
        }
        if (typeId==1){
            QueryWrapper<ManagerDetails> qw = new QueryWrapper<>();
            qw.eq("user_id",userId);
            ManagerDetails one = managerDetailsService.getOne(qw);
            if(CommonUtils.isEmpty(one)){
                return R.ok(null);
            }
            QueryWrapper<GroupManager> qw2 = new QueryWrapper<>();
            qw2.eq("manager_id",one.getId());
            List<GroupManager> list = groupManagerService.list(qw2);
            List<Integer> groupIds = new ArrayList<>();
            if(CommonUtils.isEmpty(list)){
                return R.ok(null);
            }
            for (int i = 0; i < list.size(); i++) {
                groupIds.add(list.get(i).getGroupId());
            }
            QueryWrapper<GroupEmployees> qw3 = new QueryWrapper<>();
            qw3.in("group_id",groupIds);
            List<GroupEmployees> groupEmployees = groupEmployeesService.list(qw3);
            HashSet<Integer> empIds = new HashSet<>();
            for (int i = 0; i < groupEmployees.size(); i++) {
                empIds.add(groupEmployees.get(i).getEmployeesId());
            }
            for (Integer empId : empIds) {
                employeesDetails.add(employeesDetailsService.getById(empId));
            }
        }
        return R.ok(employeesDetails);
    }

    @Override
    public R selectSuitableEmployees(String employeesId, Integer demandOrderId,Integer price) {
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("employees_id",Integer.parseInt(employeesId));
        qw.eq("demand_order_id",demandOrderId);
        int count = demandEmployeesService.count(qw);
        if(count>0){
            return R.failed("该员工已参与该需求单，请勿重复添加!");
        }
        DemandEmployees demandEmployees = new DemandEmployees();
        demandEmployees.setEmployeesId(Integer.parseInt(employeesId));
        demandEmployees.setCreateTime(LocalDateTime.now());
        demandEmployees.setDemandOrderId(demandOrderId);
        demandEmployees.setStatus(0);
        demandEmployees.setReadStatus(0);
        demandEmployees.setPrice(price);
        demandEmployees.setUserId(TokenUtils.getCurrentUserId());
        demandEmployees.setUpdateTime(LocalDateTime.now());
        demandEmployeesService.save(demandEmployees);
        return R.ok("添加保洁员成功");
    }

    @Override
    public R initiateChat(String demandOrderId) {
        /* 先检查保洁员、公司、客户全不全，完不完整 */

        return null;
    }


    @Override
    public R getAllInterestedEmployees(Integer demandOrderId) {
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("demand_order_id",demandOrderId);
        List<DemandEmployees> list = demandEmployeesService.list(qw);
        List<QuotationVo> QuotationVos = new ArrayList<>();
        if(CommonUtils.isNotEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                QuotationVo quotationVo = new QuotationVo();
                quotationVo.setId(list.get(i).getId());
                quotationVo.setDemandOrder(demandOrderService.getById(list.get(i).getDemandOrderId()));
                quotationVo.setEmployeesDetails(employeesDetailsService.getById(list.get(i).getEmployeesId()));

                List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(list.get(i).getDemandOrderId(), list.get(i).getEmployeesId());
                quotationVo.setWorkDetailsPOJOS(serviceTimeByEmployees);

                BigDecimal price = this.getPrice(serviceTimeByEmployees, list.get(i).getDemandOrderId(), list.get(i).getEmployeesId());
                quotationVo.setPrice(price);
                QuotationVos.add(quotationVo);
            }
        }
        return R.ok(QuotationVos);
    }

    @Override
    public R getInterestedByManager() {
        Integer userId = TokenUtils.getCurrentUserId();
        List<Integer> demandIds = demandEmployeesMapper.getAllDemandIds(userId);
        ArrayList<DemandEmployeesVo> demandEmployeesVos = new ArrayList<>();
        if(CommonUtils.isNotEmpty(demandIds)){
            for (int i = 0; i < demandIds.size(); i++) {
                DemandEmployeesVo demandEmployeesVo = new DemandEmployeesVo();
                demandEmployeesVo.setDemandOrder(demandOrderService.getById(demandIds.get(i)));
                QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
                qw.eq("demand_order_id",demandIds.get(i));
                qw.eq("user_id",userId);
                List<DemandEmployees> list = demandEmployeesService.list(qw);
                List<EmployeesDetails> employeesDetails = new ArrayList<>();
                for (int i1 = 0; i1 < list.size(); i1++) {
                    employeesDetails.add(employeesDetailsService.getById(list.get(i).getEmployeesId()));
                }
                demandEmployeesVo.setEmployeesDetails(employeesDetails);
                demandEmployeesVos.add(demandEmployeesVo);
            }
        }
        return R.ok(demandEmployeesVos);
    }

    @Override
    public R getInterestedByCompany() {
        Integer userId = TokenUtils.getCurrentUserId();

        QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        CompanyDetails one = companyDetailsService.getOne(qw);
        List<Integer> userIds = managerDetailsService.getAllUserIdByCompanyId(one.getId());
        List<Integer> users = demandEmployeesService.getAllUserId();
        userIds.retainAll(users);
        userIds.add(userId);

        QueryWrapper<DemandEmployees> qw2 = new QueryWrapper<>();
        qw2.in("user_id",userIds);
        List<DemandEmployees> list = demandEmployeesService.list(qw2);
        HashSet<Integer> integers = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            integers.add(list.get(i).getDemandOrderId());
        }

        ArrayList<DemandEmployeesVo> demandEmployeesVos = new ArrayList<>();

        for (Integer integer : integers) {
            DemandEmployeesVo demandEmployeesVo = new DemandEmployeesVo();
            DemandOrder byId = demandOrderService.getById(integer);
            demandEmployeesVo.setDemandOrder(byId);
            List<EmployeesDetails> employeesDetails = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getDemandOrderId().equals(integer)){
                    employeesDetails.add(employeesDetailsService.getById(list.get(i).getEmployeesId()));
                }
            }
            demandEmployeesVo.setEmployeesDetails(employeesDetails);
            demandEmployeesVos.add(demandEmployeesVo);
        }
        return R.ok(demandEmployeesVos);
    }

    @Override
    public List<WorkDetailsPOJO> getServiceTimeByEmployees(Integer demandOrderId, Integer employeesId) {

        DemandOrder demandOrder = demandOrderService.getById(demandOrderId);

        MakeAnAppointmentDTO makeAnAppointmentDTO = new MakeAnAppointmentDTO();
        makeAnAppointmentDTO.setEmployeesId(employeesId);
        makeAnAppointmentDTO.setStart(demandOrder.getStartDate());
        makeAnAppointmentDTO.setEnd(demandOrder.getEndDate());

        List<String> strings = Arrays.asList(demandOrder.getWeek().split(""));
        List<Integer> weeks = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            weeks.add(Integer.parseInt(strings.get(i)));
        }
        makeAnAppointmentDTO.setWeeks(weeks);

        List<String> jobs = Arrays.asList(demandOrder.getJobIds().split(" "));
        List<Integer> jobIds = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            jobIds.add(Integer.parseInt(jobs.get(i)));
        }
        makeAnAppointmentDTO.setJobIds(jobIds);

        List<TimeSlot> timeSlots = demandOrderMapper.getTimes(demandOrderId);
       makeAnAppointmentDTO.setTimeSlots(timeSlots);

        List<WorkDetailsPOJO> workDetailsPOJOS = employeesCalendarService.makeAnAppointmentHandle(makeAnAppointmentDTO);

        return workDetailsPOJOS;

    }

    @Override
    public BigDecimal getPrice(List<WorkDetailsPOJO> workDetails,Integer demandOrderId, Integer employeesId) {
        BigDecimal bigDecimal = employeesCalendarService.totalPrice(workDetails);

        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("demand_order_id",demandOrderId);
        qw.eq("employees_id",employeesId);
        DemandEmployees one = demandEmployeesService.getOne(qw);
        if(CommonUtils.isNotEmpty(one.getPrice())){
            bigDecimal = BigDecimal.valueOf(one.getPrice());
        }
        return bigDecimal;
    }

    @Override
    public R confirmDemand(Integer quotationId) {

        //报价单
        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        //需求单
        DemandOrder demandOrder = demandOrderService.getById(byId.getDemandOrderId());

        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();

        /* 订单来源 */
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_DEMAND);

        /* 订单编号 */
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        /* 消费项目 */
        odp.setConsumptionItems("2");

        /* 订单甲方 保洁员 */
        Boolean exist = employeesDetailsService.judgmentOfExistence(byId.getEmployeesId());
        if (!exist) return R.failed(null, "保潔員不存在");
        EmployeesDetails ed = employeesDetailsService.getById(byId.getEmployeesId());
        odp.setEmployeesId(ed.getId());
        odp.setName1(ed.getName());
        odp.setPhPrefix1(ed.getPhonePrefix());
        odp.setPhone1(ed.getPhone());

        /* 甲方所属公司 */
        CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
        odp.setCompanyId(cod.getId());
        odp.setInvoiceName(cod.getInvoiceName());
        odp.setInvoiceNumber(cod.getInvoiceNumber());


        return R.ok(new ConfirmOrderPOJO(odp), "预约成功");


    }


}
