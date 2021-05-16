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
import com.housekeeping.admin.vo.*;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
    @Resource
    private ICustomerAddressService customerAddressService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IOrderDetailsService orderDetailsService;
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
    public R suitableEmployees(Integer userId,Integer typeId,Integer  demandOrderId) {
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
        List<DemandEmployeesStatusVo> collect = employeesDetails.stream().map(x -> {
            DemandEmployeesStatusVo demandEmployeesStatusVo = new DemandEmployeesStatusVo();
            demandEmployeesStatusVo.setEmployeesDetails(x);
            QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
            qw.eq("employees_id", x.getId());
            qw.eq("demand_order_id", demandOrderId);
            int count = demandEmployeesService.count(qw);
            if (count > 0) {
                demandEmployeesStatusVo.setStatus(false);
            } else {
                demandEmployeesStatusVo.setStatus(true);
            }
            return demandEmployeesStatusVo;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public R selectSuitableEmployees(Integer employeesId, Integer demandOrderId) {
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("employees_id",employeesId);
        qw.eq("demand_order_id",demandOrderId);
        int count = demandEmployeesService.count(qw);
        if(count>0){
            return R.failed("该员工已参与该需求单，请勿重复添加!");
        }

        List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(demandOrderId, employeesId);
        BigDecimal price = this.getPrice(serviceTimeByEmployees, demandOrderId, employeesId);

        DemandEmployees demandEmployees = new DemandEmployees();
        demandEmployees.setEmployeesId(employeesId);
        demandEmployees.setCreateTime(LocalDateTime.now());
        demandEmployees.setDemandOrderId(demandOrderId);
        demandEmployees.setStatus(0);
        demandEmployees.setReadStatus(0);
        demandEmployees.setPrice(price.intValue());
        demandEmployees.setUserId(TokenUtils.getCurrentUserId());
        demandEmployees.setUpdateTime(LocalDateTime.now());
        demandEmployeesService.save(demandEmployees);
        return R.ok("添加保洁员成功");
    }



    @Override
    public R getAllInterestedEmployees(Integer demandOrderId) {
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("demand_order_id",demandOrderId);
        List<DemandEmployees> list = demandEmployeesService.list(qw);
        if(CommonUtils.isEmpty(list)){
            return R.ok(null);
        }
        List<QuotationVo> QuotationVos = new ArrayList<>();
        if(CommonUtils.isNotEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                QuotationVo quotationVo = new QuotationVo();
                quotationVo.setId(list.get(i).getId());
                quotationVo.setDemandOrder(demandOrderService.getById(list.get(i).getDemandOrderId()));
                quotationVo.setEmployeesDetails(employeesDetailsService.getById(list.get(i).getEmployeesId()));

                List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(list.get(i).getDemandOrderId(), list.get(i).getEmployeesId());
                quotationVo.setWorkDetailsPOJOS(serviceTimeByEmployees);

                quotationVo.setPrice(BigDecimal.valueOf(list.get(i).getPrice()));
                QuotationVos.add(quotationVo);
            }
        }
        return R.ok(QuotationVos);
    }

    @Override
    public R getInterestedByManager() {
        Integer userId = TokenUtils.getCurrentUserId();
        List<Integer> demandIds = demandEmployeesMapper.getAllDemandIds(userId);
        List<DemandEmployeesVo> demandEmployeesVos = new ArrayList<>();
        if(CommonUtils.isNotEmpty(demandIds)){
            for (int i = 0; i < demandIds.size(); i++) {
                DemandEmployeesVo demandEmployeesVo = new DemandEmployeesVo();
                demandEmployeesVo.setDemandOrder(demandOrderService.getById(demandIds.get(i)));
                QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
                qw.eq("demand_order_id",demandIds.get(i));
                qw.eq("user_id",userId);
                List<DemandEmployees> list = demandEmployeesService.list(qw);
                List<EmployeesDetailsDemandVo> employeesDetails = new ArrayList<>();
                for (int i1 = 0; i1 < list.size(); i1++) {
                    EmployeesDetails byId = employeesDetailsService.getById(list.get(i).getEmployeesId());
                    EmployeesDetailsDemandVo employeesDetailsDemandVo = new EmployeesDetailsDemandVo();
                    employeesDetailsDemandVo.setStatus(list.get(i).getStatus());
                    employeesDetailsDemandVo.setAccountLine(byId.getAccountLine());
                    employeesDetailsDemandVo.setAddress1(byId.getAddress1());
                    employeesDetailsDemandVo.setAddress2(byId.getAddress2());
                    employeesDetailsDemandVo.setAddress3(byId.getAddress3());
                    employeesDetailsDemandVo.setAddress4(byId.getAddress4());
                    List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(list.get(i).getDemandOrderId(), list.get(i).getEmployeesId());
                    employeesDetailsDemandVo.setWorkDetailsPOJOList(serviceTimeByEmployees);
                    BigDecimal price = this.getPrice(serviceTimeByEmployees, list.get(i).getDemandOrderId(), list.get(i).getEmployeesId());
                    if(CommonUtils.isEmpty(list.get(i).getPrice())){
                        employeesDetailsDemandVo.setPrice(price);
                    }else {
                        employeesDetailsDemandVo.setPrice(BigDecimal.valueOf(list.get(i).getPrice()));
                    }
                    employeesDetailsDemandVo.setBlacklistFlag(byId.getBlacklistFlag());
                    employeesDetailsDemandVo.setCompanyId(byId.getCompanyId());
                    employeesDetailsDemandVo.setCreateTime(byId.getCreateTime());
                    employeesDetailsDemandVo.setDateOfBirth(byId.getDateOfBirth());
                    employeesDetailsDemandVo.setDescribes(byId.getDescribes());
                    employeesDetailsDemandVo.setEducationBackground(byId.getEducationBackground());
                    employeesDetailsDemandVo.setEmail(byId.getEmail());
                    employeesDetailsDemandVo.setEngName(byId.getEngName());
                    employeesDetailsDemandVo.setWorkYear(byId.getWorkYear());
                    employeesDetailsDemandVo.setWorkingArea(byId.getWorkingArea());
                    employeesDetailsDemandVo.setUserId(byId.getUserId());
                    employeesDetailsDemandVo.setUpdateTime(byId.getUpdateTime());
                    employeesDetailsDemandVo.setTags(byId.getTags());
                    employeesDetailsDemandVo.setStarRating(byId.getStarRating());
                    employeesDetailsDemandVo.setSex(byId.getSex());
                    employeesDetailsDemandVo.setScopeOfOrder(byId.getScopeOfOrder());
                    employeesDetailsDemandVo.setRecordOfFormalSchooling(byId.getRecordOfFormalSchooling());
                    employeesDetailsDemandVo.setPresetJobIds(byId.getPresetJobIds());
                    employeesDetailsDemandVo.setPhoneUrl(byId.getPhoneUrl());
                    employeesDetailsDemandVo.setPhonePrefix(byId.getPhonePrefix());
                    employeesDetailsDemandVo.setNumberOfOrders(byId.getNumberOfOrders());
                    employeesDetailsDemandVo.setName(byId.getName());
                    employeesDetailsDemandVo.setLng(byId.getLng());
                    employeesDetailsDemandVo.setLat(byId.getLat());
                    employeesDetailsDemandVo.setLastReviserId(byId.getLastReviserId());
                    employeesDetailsDemandVo.setIdCard(byId.getIdCard());
                    employeesDetailsDemandVo.setHeadUrl(byId.getHeadUrl());
                    employeesDetailsDemandVo.setId(byId.getId());
                    employeesDetailsDemandVo.setDemandEmployeesId(list.get(i).getId());
                    employeesDetails.add(employeesDetailsDemandVo);
                }
                demandEmployeesVo.setEmployeesDetailsDemandVos(employeesDetails);
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
            List<EmployeesDetailsDemandVo> employeesDetails = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getDemandOrderId().equals(integer)){

                    EmployeesDetails employees = employeesDetailsService.getById(list.get(i).getEmployeesId());
                    EmployeesDetailsDemandVo employeesDetailsDemandVo = new EmployeesDetailsDemandVo();
                    employeesDetailsDemandVo.setStatus(list.get(i).getStatus());
                    employeesDetailsDemandVo.setAccountLine(employees.getAccountLine());
                    employeesDetailsDemandVo.setAddress1(employees.getAddress1());
                    employeesDetailsDemandVo.setAddress2(employees.getAddress2());
                    employeesDetailsDemandVo.setAddress3(employees.getAddress3());
                    employeesDetailsDemandVo.setAddress4(employees.getAddress4());
                    List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(list.get(i).getDemandOrderId(), list.get(i).getEmployeesId());
                    employeesDetailsDemandVo.setWorkDetailsPOJOList(serviceTimeByEmployees);
                    BigDecimal price = this.getPrice(serviceTimeByEmployees, list.get(i).getDemandOrderId(), list.get(i).getEmployeesId());
                    if(CommonUtils.isEmpty(list.get(i).getPrice())){
                        employeesDetailsDemandVo.setPrice(price);
                    }else {
                        employeesDetailsDemandVo.setPrice(BigDecimal.valueOf(list.get(i).getPrice()));
                    }
                    employeesDetailsDemandVo.setBlacklistFlag(employees.getBlacklistFlag());
                    employeesDetailsDemandVo.setCompanyId(employees.getCompanyId());
                    employeesDetailsDemandVo.setCreateTime(employees.getCreateTime());
                    employeesDetailsDemandVo.setDateOfBirth(employees.getDateOfBirth());
                    employeesDetailsDemandVo.setDescribes(employees.getDescribes());
                    employeesDetailsDemandVo.setEducationBackground(employees.getEducationBackground());
                    employeesDetailsDemandVo.setEmail(employees.getEmail());
                    employeesDetailsDemandVo.setEngName(employees.getEngName());
                    employeesDetailsDemandVo.setWorkYear(employees.getWorkYear());
                    employeesDetailsDemandVo.setWorkingArea(employees.getWorkingArea());
                    employeesDetailsDemandVo.setUserId(employees.getUserId());
                    employeesDetailsDemandVo.setUpdateTime(employees.getUpdateTime());
                    employeesDetailsDemandVo.setTags(employees.getTags());
                    employeesDetailsDemandVo.setStarRating(employees.getStarRating());
                    employeesDetailsDemandVo.setSex(employees.getSex());
                    employeesDetailsDemandVo.setScopeOfOrder(employees.getScopeOfOrder());
                    employeesDetailsDemandVo.setRecordOfFormalSchooling(employees.getRecordOfFormalSchooling());
                    employeesDetailsDemandVo.setPresetJobIds(employees.getPresetJobIds());
                    employeesDetailsDemandVo.setPhoneUrl(employees.getPhoneUrl());
                    employeesDetailsDemandVo.setPhonePrefix(employees.getPhonePrefix());
                    employeesDetailsDemandVo.setNumberOfOrders(employees.getNumberOfOrders());
                    employeesDetailsDemandVo.setName(employees.getName());
                    employeesDetailsDemandVo.setLng(employees.getLng());
                    employeesDetailsDemandVo.setLat(employees.getLat());
                    employeesDetailsDemandVo.setLastReviserId(employees.getLastReviserId());
                    employeesDetailsDemandVo.setIdCard(employees.getIdCard());
                    employeesDetailsDemandVo.setHeadUrl(employees.getHeadUrl());
                    employeesDetailsDemandVo.setId(employees.getId());
                    employeesDetailsDemandVo.setDemandEmployeesId(list.get(i).getId());
                    employeesDetails.add(employeesDetailsDemandVo);
                }
            }
            demandEmployeesVo.setEmployeesDetailsDemandVos(employeesDetails);
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

        List<String> strings = Arrays.asList(demandOrder.getWeek().split(","));
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

        /*QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("demand_order_id",demandOrderId);
        qw.eq("employees_id",employeesId);
        DemandEmployees one = demandEmployeesService.getOne(qw);
        if(CommonUtils.isNotEmpty(one.getPrice())){
            bigDecimal = BigDecimal.valueOf(one.getPrice());
        }*/
        return bigDecimal;
    }

    public Float hOfDay(List<TimeSlot> timeSlots) {
        AtomicReference<Float> h  = new AtomicReference<>(new Float(0));
        timeSlots.forEach(timeSlot -> {
            h.set(h.get() + timeSlot.getTimeSlotLength());
        });
        return h.get();
    }

    @Override
    public R confirmDemand(Integer quotationId) {

        //报价单
        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        //需求单
        DemandOrder demandOrder = demandOrderService.getById(byId.getDemandOrderId());
        if(demandOrder.getStatus().equals(1)){
            return R.failed("该订单已接，请勿重复确认!");
        }

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

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());

        odp.setCustomerId(cd.getId());
        odp.setName2(demandOrder.getCustomerName());
        odp.setPhPrefix2(demandOrder.getPhonePrefix());
        odp.setPhone2(demandOrder.getPhone());

        /* 订单工作内容 */
        String jobIds = demandOrder.getJobIds();
        odp.setJobIds(jobIds);

        /* 地址 */
        odp.setAddress(demandOrder.getAddress());
        odp.setLat(new Float(demandOrder.getLat()));
        odp.setLng(new Float(demandOrder.getLng()));


        /* 工作时间安排 */
        List<WorkDetailsPOJO> wds = this.getServiceTimeByEmployees(byId.getDemandOrderId(), byId.getEmployeesId());
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = employeesCalendarService.days(wds);
        odp.setDays(days);

        List<TimeSlot> times = demandOrderMapper.getTimes(byId.getDemandOrderId());
        /* 每日工作时长计算 */
        Float h = this.hOfDay(times);
        odp.setHOfDay(h);

        /* 原价格计算 */
        BigDecimal pdb = this.getPrice(wds,byId.getDemandOrderId(), byId.getEmployeesId());
        odp.setPriceBeforeDiscount(pdb);
        odp.setPriceAfterDiscount(pdb);

        /* 订单状态 */
        odp.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);//待支付状态

        /* 订单生成时间 */
        odp.setStartDateTime(now);
        odp.setUpdateDateTime(now);

        /* 订单截止付款时间 保留时间 */
        Integer hourly = orderDetailsService.orderRetentionTime(byId.getEmployeesId());
        LocalDateTime payDeadline = now.plusHours(hourly);
        odp.setPayDeadline(payDeadline);
        odp.setH(hourly);

        String key = "OrderToBePaid:employeesId"+byId.getEmployeesId()+":" + number;
        Map<String, Object> map = new HashMap<>();
        try {
            map = CommonUtils.objectToMap(odp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, hourly, TimeUnit.HOURS);

        byId.setStatus(1);
        demandEmployeesService.updateById(byId);

        demandOrder.setStatus(1);
        demandOrderService.updateById(demandOrder);

        return R.ok(new ConfirmOrderPOJO(odp), "确认报价成功");


    }

    @Override
    public R changePrice(String quotationId, Integer price) {
        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        byId.setPrice(price);
        demandEmployeesService.updateById(byId);
        return R.ok("修改成功");
    }

    @Override
    public R cusRemove(Integer id) {
        DemandEmployees byId = demandEmployeesService.getById(id);
        if(byId.getStatus()==1){
            return R.failed("该报价单已被确认，无法删除！");
        }
        demandEmployeesService.removeById(id);
        return R.ok("删除成功!");
    }

    @Override
    public R cusGetById(Integer quotationId) {

        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        if(CommonUtils.isEmpty(byId)){
            return R.failed("该报价单为空");
        }
        QuotationVo quotationVo = new QuotationVo();
        quotationVo.setId(byId.getId());
        quotationVo.setDemandOrder(demandOrderService.getById(byId.getDemandOrderId()));
        quotationVo.setEmployeesDetails(employeesDetailsService.getById(byId.getEmployeesId()));

        List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(byId.getDemandOrderId(), byId.getEmployeesId());
        quotationVo.setWorkDetailsPOJOS(serviceTimeByEmployees);

        quotationVo.setPrice(BigDecimal.valueOf(byId.getPrice()));

        return R.ok(quotationVo);
    }


}
