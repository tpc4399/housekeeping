package com.housekeeping.admin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.housekeeping.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private IReleaseRequirementService releaseRequirementService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private ISysJobContendService jobContendService;
    @Resource
    private ISysIndexService sysIndexService;
    @Resource
    private ISerialNumberService serialNumberService;
    @Resource
    private ISerialService serialService;
    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private ISysConfigService configService;

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
    public R suitableEmployees(Integer userId,Integer typeId,Integer  demandOrderId,String empName) {
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

        List<EmployeesDetails> search;

        if(StringUtils.isNotBlank(empName)){
            search = search(empName, employeesDetails);
        }else{
            search = employeesDetails;
        }

        List<DemandEmployeesStatusVo> collect = search.stream().map(x -> {
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

        collect.forEach(x ->{
            EmployeesDetails byId = x.getEmployeesDetails();
            String presetJobIds = byId.getPresetJobIds();
            List<Skill> skills = new ArrayList<>();
            if(StringUtils.isNotBlank(presetJobIds)){
                List<String> strings = Arrays.asList(presetJobIds.split(" "));
                if(CollectionUtils.isNotEmpty(strings)){
                    for (int i = 0; i < strings.size(); i++) {
                        Skill skill = new Skill();
                        skill.setJobId(Integer.parseInt(strings.get(i)));
                        skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(i))).getContend());
                        skills.add(skill);
                    }
                }
            }
            x.setSkills(skills);
        });

        return R.ok(collect);
    }

    public List<EmployeesDetails> search(String name, List<EmployeesDetails> list){
        List<EmployeesDetails> results = new ArrayList();
        Pattern pattern = Pattern.compile(name);
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((EmployeesDetails)list.get(i)).getName());
            if(matcher.find()){
                results.add(list.get(i));
            }
        }
        return results;
    }

    @Override
    public R sendOffer(Integer employeesId, Integer demandOrderId,BigDecimal price) {
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("employees_id",employeesId);
        qw.eq("demand_order_id",demandOrderId);
        int count = demandEmployeesService.count(qw);
        if(count>0){
            return R.failed("该员工已参与该需求单，请勿重复添加!");
        }

        BigDecimal offerPrice;

        List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(demandOrderId, employeesId);

        if(CommonUtils.isNotEmpty(price)){
            offerPrice = price;
        }else {
            offerPrice = this.getPrice(serviceTimeByEmployees, demandOrderId, employeesId);
        }
        DemandEmployees demandEmployees = new DemandEmployees();
        demandEmployees.setEmployeesId(employeesId);
        demandEmployees.setCreateTime(LocalDateTime.now());
        demandEmployees.setDemandOrderId(demandOrderId);
        demandEmployees.setStatus(0);
        demandEmployees.setReadStatus(0);
        demandEmployees.setPrice(offerPrice.intValue());
        demandEmployees.setUserId(TokenUtils.getCurrentUserId());
        demandEmployees.setUpdateTime(LocalDateTime.now());
        demandEmployeesService.save(demandEmployees);
        return R.ok("发送成功");
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
                DemandOrder byId = demandOrderService.getById(list.get(i).getDemandOrderId());
                quotationVo.setDemandOrder(byId);
                EmployeesDetails byId1 = employeesDetailsService.getById(list.get(i).getEmployeesId());
                quotationVo.setEmployeesDetails(byId1);

                List<WorkDetailsPOJO> workDetailsPOJOS = JSONArray.parseArray(list.get(i).getWorkTime(), WorkDetailsPOJO.class);
                quotationVo.setWorkDetailsPOJOS(workDetailsPOJOS);

                quotationVo.setPrice(BigDecimal.valueOf(list.get(i).getPrice()));

                quotationVo.setStatus(this.getStatus(list.get(i)));

                //需求单工作内容
                String jobs = byId.getJobIds();
                List<Skill> skills = new ArrayList<>();
                List<String> strings = Arrays.asList(jobs.split(" "));
                if(CommonUtils.isNotEmpty(strings)){
                    for (int x = 0; x < strings.size(); x++) {
                        Skill skill = new Skill();
                        int id = Integer.parseInt(strings.get(x));
                        skill.setJobId(id);
                        skill.setContend(sysJobContendService.getById(id).getContend());
                        skills.add(skill);
                    }
                    quotationVo.setWorkContent(skills);
                }else {
                    quotationVo.setWorkContent(null);
                }

                //需求单工作类型
                String type = byId.getParentId();
                List<Skill> types = new ArrayList<>();
                List<String> strings1 = Arrays.asList(type.split(" "));
                for (int x = 0; x < strings1.size(); x++) {
                    Skill skill = new Skill();
                    int id = Integer.parseInt(strings1.get(x));
                    skill.setJobId(id);
                    skill.setContend(sysIndexService.getById(id).getName());
                    types.add(skill);
                }
                quotationVo.setWorkType(types);

                String attendant = list.get(i).getAttendant();
                if(attendant==null||attendant.equals("")){
                    quotationVo.setEmployeesDetailsList(new ArrayList<EmployeesDetails>());
                }else{
                    ArrayList<EmployeesDetails> detailsArrayList = new ArrayList<>();
                    List<String> strings2 = Arrays.asList(attendant.split(" "));
                    for (String s : strings2) {
                        EmployeesDetails byId2 = employeesDetailsService.getById(Integer.parseInt(s));
                        if(byId2!=null){
                            detailsArrayList.add(byId2);
                        }
                    }
                    quotationVo.setEmployeesDetailsList(detailsArrayList);
                }

                QuotationVos.add(quotationVo);

                /** certified:員工認證準備 */
                Integer certified;
                //不属于公司就是个体户 1个体户 2工作室 3公司
                CompanyDetails cd = companyDetailsService.getById(byId1.getCompanyId());
                if(cd.getIsPersonal()==true){
                    certified = 1;
                }else {
                    Boolean isCertified = cd.getIsValidate();
                    if(isCertified==false){
                        certified = 2;
                    }else {
                        certified = 3;
                    }
                }
                quotationVo.setCertified(certified);
            }
        }
        return R.ok(QuotationVos);
    }

    @Override
    public R getInterestedByManager(Integer managerId) {
        Integer userId = managerDetailsService.getById(managerId).getUserId();
        List<Integer> demandIds = demandEmployeesMapper.getAllDemandIds(userId);
        List<DemandEmployeesVo> demandEmployeesVos = new ArrayList<>();
        if(CommonUtils.isNotEmpty(demandIds)){
            for (int i = 0; i < demandIds.size(); i++) {
                DemandEmployeesVo demandEmployeesVo = new DemandEmployeesVo();
                DemandOrder byId1 = demandOrderService.getById(demandIds.get(i));
                Integer status = releaseRequirementService.getStatus(byId1);
                byId1.setStatus(status);
                demandEmployeesVo.setCustomerDetails(customerDetailsService.getById(byId1.getCustomerId()));
                demandEmployeesVo.setDemandOrder(byId1);
                //需求单工作内容
                String jobs = byId1.getJobIds();
                List<Skill> skills = new ArrayList<>();
                List<String> strings = Arrays.asList(jobs.split(" "));
                for (int x = 0; x < strings.size(); x++) {
                    Skill skill = new Skill();
                    int id = Integer.parseInt(strings.get(x));
                    skill.setJobId(id);
                    skill.setContend(jobContendService.getById(id).getContend());
                    skills.add(skill);
                }
                demandEmployeesVo.setWorkContent(skills);

                //需求单工作类型
                String type = byId1.getParentId();
                List<Skill> types = new ArrayList<>();
                List<String> strings1 = Arrays.asList(type.split(" "));
                for (int x = 0; x < strings1.size(); x++) {
                    Skill skill = new Skill();
                    int id = Integer.parseInt(strings1.get(x));
                    skill.setJobId(id);
                    skill.setContend(sysIndexService.getById(id).getName());
                    types.add(skill);
                }
                demandEmployeesVo.setWorkType(types);

                QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
                qw.eq("demand_order_id",demandIds.get(i));
                qw.eq("user_id",userId);
                List<DemandEmployees> list = demandEmployeesService.list(qw);
                List<EmployeesDetailsDemandVo> employeesDetails = new ArrayList<>();
                for (int i1 = 0; i1 < list.size(); i1++) {
                    EmployeesDetailsDemandVo detailsDemandVo = new EmployeesDetailsDemandVo();
                    detailsDemandVo.setId(list.get(i1).getId());
                    detailsDemandVo.setStatus(this.getStatus(list.get(i1)));
                    detailsDemandVo.setPrice(list.get(i1).getPrice());
                    detailsDemandVo.setDemandOrder(demandOrderService.getById(list.get(i1).getDemandOrderId()));
                    detailsDemandVo.setEmployeesDetails(employeesDetailsService.getById(list.get(i1).getEmployeesId()));
                    detailsDemandVo.setCreateTime(list.get(i1).getCreateTime());

                    String attendant = list.get(i).getAttendant();
                    if(attendant==null||attendant.equals("")){
                        detailsDemandVo.setEmployeesDetailsList(new ArrayList<EmployeesDetails>());
                    }else{
                        ArrayList<EmployeesDetails> detailsArrayList = new ArrayList<>();
                        List<String> strings2 = Arrays.asList(attendant.split(" "));
                        for (String s : strings2) {
                            EmployeesDetails byId2 = employeesDetailsService.getById(Integer.parseInt(s));
                            if(byId2!=null){
                                detailsArrayList.add(byId2);
                            }
                        }
                        detailsDemandVo.setEmployeesDetailsList(detailsArrayList);
                    }

                    List<WorkDetailsPOJO> workDetailsPOJOS = JSONArray.parseArray(list.get(i1).getWorkTime(), WorkDetailsPOJO.class);
                    detailsDemandVo.setWorkDetailsPOJOS(workDetailsPOJOS);
                    employeesDetails.add(detailsDemandVo);
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
            Integer status = releaseRequirementService.getStatus(byId);
            byId.setStatus(status);
            demandEmployeesVo.setDemandOrder(byId);
            demandEmployeesVo.setCustomerDetails(customerDetailsService.getById(byId.getCustomerId()));
            //需求单工作内容
            String jobs = byId.getJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(jobs.split(" "));
            for (int x = 0; x < strings.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings.get(x));
                skill.setJobId(id);
                skill.setContend(jobContendService.getById(id).getContend());
                skills.add(skill);
            }
            demandEmployeesVo.setWorkContent(skills);

            //需求单工作类型
            String type = byId.getParentId();
            List<Skill> types = new ArrayList<>();
            List<String> strings1 = Arrays.asList(type.split(" "));
            for (int x = 0; x < strings1.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings1.get(x));
                skill.setJobId(id);
                skill.setContend(sysIndexService.getById(id).getName());
                types.add(skill);
            }
            demandEmployeesVo.setWorkType(types);

            List<EmployeesDetailsDemandVo> employeesDetails = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getDemandOrderId().equals(integer)){
                    EmployeesDetailsDemandVo detailsDemandVo = new EmployeesDetailsDemandVo();
                    detailsDemandVo.setId(list.get(i).getId());
                    detailsDemandVo.setStatus(this.getStatus(list.get(i)));
                    detailsDemandVo.setPrice(list.get(i).getPrice());
                    detailsDemandVo.setDemandOrder(demandOrderService.getById(list.get(i).getDemandOrderId()));
                    detailsDemandVo.setEmployeesDetails(employeesDetailsService.getById(list.get(i).getEmployeesId()));
                    detailsDemandVo.setCreateTime(list.get(i).getCreateTime());

                    String attendant = list.get(i).getAttendant();
                    if(attendant==null||attendant.equals("")){
                        detailsDemandVo.setEmployeesDetailsList(new ArrayList<EmployeesDetails>());
                    }else{
                        ArrayList<EmployeesDetails> detailsArrayList = new ArrayList<>();
                        List<String> strings2 = Arrays.asList(attendant.split(" "));
                        for (String s : strings2) {
                            EmployeesDetails byId2 = employeesDetailsService.getById(Integer.parseInt(s));
                            if(byId2!=null){
                                detailsArrayList.add(byId2);
                            }
                        }
                        detailsDemandVo.setEmployeesDetailsList(detailsArrayList);
                    }

                    List<WorkDetailsPOJO> workDetailsPOJOS = JSONArray.parseArray(list.get(i).getWorkTime(), WorkDetailsPOJO.class);
                    detailsDemandVo.setWorkDetailsPOJOS(workDetailsPOJOS);
                    employeesDetails.add(detailsDemandVo);
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

        Integer jobId = jobIds.get(0);

        List<WorkDetailsPOJO> workDetailsPOJOS = employeesCalendarService.makeAnAppointmentHandles(makeAnAppointmentDTO,true,jobId);

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
        if(releaseRequirementService.getStatus(demandOrder).equals(1)){
            return R.failed("该订单已接，请勿重复确认!");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();

        /* 订单来源 */
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_DEMAND);

        /* 订单编号 */
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        /* 流水号 */
        String serialNumber = serialNumberService.generateSerialNumber(number);
        odp.setSerialNumber(serialNumber);

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
        odp.setCompanyId(cod==null?"":cod.getId().toString());
        odp.setInvoiceName(cod==null?null:cod.getInvoiceName());
        odp.setInvoiceNumber(cod==null?null:cod.getInvoiceNumber());

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());

        odp.setCustomerId(cd.getId());
        odp.setName2(demandOrder.getCustomerName());
        odp.setPhPrefix2(demandOrder.getPhonePrefix());
        odp.setPhone2(demandOrder.getPhone());

        /* 订单工作内容 */
        String jobIds = demandOrder.getJobIds();
        odp.setJobIds(jobIds);

        odp.setNoteIds(null);

        /* 地址 */
        odp.setAddress(demandOrder.getAddress());
        odp.setLat(new Float(demandOrder.getLat()));
        odp.setLng(new Float(demandOrder.getLng()));


        /* 工作时间安排 */
        List<WorkDetailsPOJO> wds = JSONArray.parseArray(byId.getWorkTime(), WorkDetailsPOJO.class);
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = employeesCalendarService.days(wds);
        odp.setDays(days);

        List<TimeSlot> times = demandOrderMapper.getTimes(byId.getDemandOrderId());
        /* 每日工作时长计算 */
        Float h = this.hOfDay(times);
        odp.setHOfDay(h);

        /* 原价格计算 */
        if(CommonUtils.isNotEmpty(byId.getPrice())){
            odp.setPriceBeforeDiscount(BigDecimal.valueOf(byId.getPrice()));

            //媒合费
            QueryWrapper qw = new QueryWrapper();
            qw.eq("config_key", "matchmakingFeeFloat");
            SysConfig one = configService.getOne(qw);
            odp.setMatchmakingFee(BigDecimal.valueOf(byId.getPrice()).multiply(new BigDecimal(one.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

            //系统服务费
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("config_key", "systemServiceFeeFloat");
            SysConfig one2 = configService.getOne(qw2);
            odp.setSystemServiceFee(BigDecimal.valueOf(byId.getPrice()).multiply(new BigDecimal(one2.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

            //刷卡手续费
            QueryWrapper qw3 = new QueryWrapper();
            qw3.eq("config_key", "servicesChargeForCreditCardFloat");
            SysConfig one3 = configService.getOne(qw3);
            odp.setCardSwipeFee(BigDecimal.valueOf(byId.getPrice()).multiply(new BigDecimal(one3.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

            //平台費（客戶）
            QueryWrapper qw4 = new QueryWrapper();
            qw4.eq("config_key", "platformFeeCus");
            SysConfig one4 = configService.getOne(qw4);
            BigDecimal bigDecimal = new BigDecimal(one4.getConfigValue());
            odp.setPlatformFeeCus(bigDecimal);

            //平台費（員工）
            QueryWrapper qw5 = new QueryWrapper();
            qw5.eq("config_key", "platformFeeEmp");
            SysConfig one5 = configService.getOne(qw5);
            odp.setPlatformFeeEmp(new BigDecimal(one5.getConfigValue()));


            //服务费+订单费
            odp.setPriceAfterDiscount(BigDecimal.valueOf(byId.getPrice()).add(bigDecimal));

        }else {
            BigDecimal pdb = this.getPrice(wds,byId.getDemandOrderId(), byId.getEmployeesId());
            odp.setPriceBeforeDiscount(pdb);

            //媒合费
            QueryWrapper qw = new QueryWrapper();
            qw.eq("config_key", "matchmakingFeeFloat");
            SysConfig one = configService.getOne(qw);
            odp.setMatchmakingFee(pdb.multiply(new BigDecimal(one.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

            //系统服务费
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("config_key", "systemServiceFeeFloat");
            SysConfig one2 = configService.getOne(qw2);
            odp.setSystemServiceFee(pdb.multiply(new BigDecimal(one2.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

            //刷卡手续费
            QueryWrapper qw3 = new QueryWrapper();
            qw3.eq("config_key", "servicesChargeForCreditCardFloat");
            SysConfig one3 = configService.getOne(qw3);
            odp.setCardSwipeFee(pdb.multiply(new BigDecimal(one3.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

            //平台費（客戶）
            QueryWrapper qw4 = new QueryWrapper();
            qw4.eq("config_key", "platformFeeCus");
            SysConfig one4 = configService.getOne(qw4);
            BigDecimal bigDecimal = new BigDecimal(one4.getConfigValue());
            odp.setPlatformFeeCus(bigDecimal);

            //平台費（員工）
            QueryWrapper qw5 = new QueryWrapper();
            qw5.eq("config_key", "platformFeeEmp");
            SysConfig one5 = configService.getOne(qw5);
            odp.setPlatformFeeEmp(new BigDecimal(one5.getConfigValue()));

            //平台費（客戶）+订单费
            odp.setPriceAfterDiscount(pdb.add(bigDecimal));
        }

        /* 订单状态 */
        odp.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);//待支付状态

        /* 订单生成时间 */
        odp.setStartDateTime(now);
        odp.setUpdateDateTime(now);

        /* 订单截止付款时间 保留时间 */
        Integer hourly = null;
        if(cod==null){
            QueryWrapper<SysConfig> qw = new QueryWrapper();
            qw.eq("config_key", ApplicationConfigConstants.orderRetentionTime);
            SysConfig one = configService.getOne(qw);
            int hour = Integer.parseInt(one.getConfigValue());
            hourly = hour;
        }
        else {
            hourly = orderDetailsService.orderRetentionTime(byId.getEmployeesId());
        }
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
        serialService.generatePipeline(odp);

        byId.setStatus(1);
        byId.setOrderNumber(number.toString());
        demandEmployeesService.updateById(byId);

        demandOrder.setStatus(1);
        demandOrderService.updateById(demandOrder);

        return R.ok(new ConfirmOrderPOJO(odp), "确认报价成功");

    }

    @Override
    public R changePrice(String quotationId, Integer price) {
        DemandEmployees byId1 = demandEmployeesService.getById(quotationId);
        if(this.getStatus(byId1).equals(1)){
            return R.failed("该报价单已被确认，无法修改！");
        }
        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        byId.setPrice(price);
        demandEmployeesService.updateById(byId);
        return R.ok("修改成功");
    }

    @Override
    public R cusRemove(Integer id) {
        DemandEmployees byId = demandEmployeesService.getById(id);
        if(this.getStatus(byId).equals(1)){
            return R.failed("该报价单已被确认，无法删除！");
        }
        demandEmployeesService.removeById(id);
        return R.ok("删除成功!");
    }

    @Override
    public QuotationVo cusGetById(Integer quotationId) {

        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        if(CommonUtils.isEmpty(byId)){
            return null;
        }
        QuotationVo quotationVo = new QuotationVo();
        quotationVo.setId(byId.getId());
        DemandOrder byId1 = demandOrderService.getById(byId.getDemandOrderId());
        byId1.setStatus(releaseRequirementService.getStatus(byId1));
        quotationVo.setDemandOrder(byId1);
        quotationVo.setEmployeesDetails(employeesDetailsService.getById(byId.getEmployeesId()));

        List<WorkDetailsPOJO> workDetailsPOJOS = JSONArray.parseArray(byId.getWorkTime(), WorkDetailsPOJO.class);
        quotationVo.setWorkDetailsPOJOS(workDetailsPOJOS);

        quotationVo.setPrice(BigDecimal.valueOf(byId.getPrice()));

        String attendant = byId.getAttendant();
        if(attendant==null||attendant.equals("")){
            quotationVo.setEmployeesDetailsList(new ArrayList<EmployeesDetails>());
        }else{
            ArrayList<EmployeesDetails> detailsArrayList = new ArrayList<>();
            List<String> strings2 = Arrays.asList(attendant.split(" "));
            for (String s : strings2) {
                EmployeesDetails byId2 = employeesDetailsService.getById(Integer.parseInt(s));
                if(byId2!=null){
                    detailsArrayList.add(byId2);
                }
            }
            quotationVo.setEmployeesDetailsList(detailsArrayList);
        }

        Integer status = this.getStatus(byId);
        quotationVo.setStatus(status);

        return quotationVo;
    }

    @Override
    public R selectSuitableEmployees(Integer employeesId, Integer demandOrderId,List<Integer> attendant) {
        QuotationVo quotationVo = new QuotationVo();
        quotationVo.setId(null);

        List<WorkDetailsPOJO> serviceTimeByEmployees = this.getServiceTimeByEmployees(demandOrderId, employeesId);
        quotationVo.setWorkDetailsPOJOS(serviceTimeByEmployees);

        BigDecimal price = this.getPrice(serviceTimeByEmployees, demandOrderId, employeesId);
        quotationVo.setPrice(price);

        quotationVo.setEmployeesDetails(employeesDetailsService.getById(employeesId));
        quotationVo.setDemandOrder(demandOrderService.getById(demandOrderId));

        /* 可工作天数计算 */
        Integer days = employeesCalendarService.days(serviceTimeByEmployees);
        quotationVo.setDays(days);

        List<TimeSlot> times = demandOrderMapper.getTimes(demandOrderId);
        /* 每日工作时长计算 */
        Float h = this.hOfDay(times);
        quotationVo.setH(h);

        if(CollectionUtils.isNotEmpty(attendant)){
            List<EmployeesDetails> collect = attendant.stream().map(x -> employeesDetailsService.getById(x)).collect(Collectors.toList());
            quotationVo.setEmployeesDetailsList(collect);
        }else{
            quotationVo.setEmployeesDetailsList(new ArrayList<EmployeesDetails>());
        }

        return R.ok(quotationVo);
    }

    public Integer getStatus(DemandEmployees demandEmployees){
        Integer state = orderDetailsService.getState(demandEmployees.getOrderNumber());
        if(state.equals(-1)||state.equals(0)){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public R getAllQuotationByAdmin(Page page) {
        List<DemandEmployees> list = demandEmployeesService.list();
        List<QuotationVo> collect = list.stream().map(x -> {
            QuotationVo quotationVo = new QuotationVo();
            quotationVo.setId(x.getId());
            DemandOrder byId1 = demandOrderService.getById(x.getDemandOrderId());
            byId1.setStatus(releaseRequirementService.getStatus(byId1));
            quotationVo.setDemandOrder(byId1);
            quotationVo.setEmployeesDetails(employeesDetailsService.getById(x.getEmployeesId()));

            List<WorkDetailsPOJO> workDetailsPOJOS = JSONArray.parseArray(x.getWorkTime(), WorkDetailsPOJO.class);
            quotationVo.setWorkDetailsPOJOS(workDetailsPOJOS);

            quotationVo.setPrice(BigDecimal.valueOf(x.getPrice()));

            Integer status = this.getStatus(x);
            quotationVo.setStatus(status);

            return quotationVo;
        }).collect(Collectors.toList());
        if(CommonUtils.isEmpty(collect)){
            return R.ok(null);
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), collect);
        return R.ok(pages);
    }

    @Override
    public R getInterestedByEmp(Integer employeesId) {
        List<Integer> demandIds = demandEmployeesMapper.getAllDemandIdsByEmpId(employeesId);
        List<DemandEmployeesVo> demandEmployeesVos = new ArrayList<>();
        if(CommonUtils.isNotEmpty(demandIds)){
            for (int i = 0; i < demandIds.size(); i++) {
                DemandEmployeesVo demandEmployeesVo = new DemandEmployeesVo();
                DemandOrder byId1 = demandOrderService.getById(demandIds.get(i));
                Integer status = releaseRequirementService.getStatus(byId1);
                byId1.setStatus(status);
                demandEmployeesVo.setDemandOrder(byId1);
                demandEmployeesVo.setCustomerDetails(customerDetailsService.getById(byId1.getCustomerId()));
                //需求单工作内容
                String jobs = byId1.getJobIds();
                List<Skill> skills = new ArrayList<>();
                List<String> strings = Arrays.asList(jobs.split(" "));
                for (int x = 0; x < strings.size(); x++) {
                    Skill skill = new Skill();
                    int id = Integer.parseInt(strings.get(x));
                    skill.setJobId(id);
                    skill.setContend(jobContendService.getById(id).getContend());
                    skills.add(skill);
                }
                demandEmployeesVo.setWorkContent(skills);

                //需求单工作类型
                String type = byId1.getParentId();
                List<Skill> types = new ArrayList<>();
                List<String> strings1 = Arrays.asList(type.split(" "));
                for (int x = 0; x < strings1.size(); x++) {
                    Skill skill = new Skill();
                    int id = Integer.parseInt(strings1.get(x));
                    skill.setJobId(id);
                    skill.setContend(sysIndexService.getById(id).getName());
                    types.add(skill);
                }
                demandEmployeesVo.setWorkType(types);

                QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
                qw.eq("demand_order_id",demandIds.get(i));
                qw.eq("employees_id",employeesId);
                List<DemandEmployees> list = demandEmployeesService.list(qw);
                List<EmployeesDetailsDemandVo> employeesDetails = new ArrayList<>();
                for (int i1 = 0; i1 < list.size(); i1++) {
                    EmployeesDetailsDemandVo detailsDemandVo = new EmployeesDetailsDemandVo();
                    detailsDemandVo.setId(list.get(i1).getId());
                    detailsDemandVo.setStatus(this.getStatus(list.get(i1)));
                    detailsDemandVo.setPrice(list.get(i1).getPrice());
                    detailsDemandVo.setDemandOrder(demandOrderService.getById(list.get(i1).getDemandOrderId()));
                    detailsDemandVo.setEmployeesDetails(employeesDetailsService.getById(list.get(i1).getEmployeesId()));
                    detailsDemandVo.setCreateTime(list.get(i1).getCreateTime());

                    String attendant = list.get(i1).getAttendant();
                    if(attendant==null||attendant.equals("")){
                        detailsDemandVo.setEmployeesDetailsList(new ArrayList<EmployeesDetails>());
                    }else{
                        ArrayList<EmployeesDetails> detailsArrayList = new ArrayList<>();
                        List<String> strings2 = Arrays.asList(attendant.split(" "));
                        for (String s : strings2) {
                            EmployeesDetails byId2 = employeesDetailsService.getById(Integer.parseInt(s));
                            if(byId2!=null){
                                detailsArrayList.add(byId2);
                            }
                        }
                        detailsDemandVo.setEmployeesDetailsList(detailsArrayList);
                    }

                    List<WorkDetailsPOJO> workDetailsPOJOS = JSONArray.parseArray(list.get(i1).getWorkTime(), WorkDetailsPOJO.class);
                    detailsDemandVo.setWorkDetailsPOJOS(workDetailsPOJOS);
                    employeesDetails.add(detailsDemandVo);
                }
                demandEmployeesVo.setEmployeesDetailsDemandVos(employeesDetails);
                demandEmployeesVos.add(demandEmployeesVo);
            }
        }
        return R.ok(demandEmployeesVos);
    }

    @Override
    public R newSendOffer(Map map) {
        Integer employeesId = (Integer) map.remove("employeesId");
        Integer demandOrderId = (Integer) map.remove("demandOrderId");
        Integer price = (Integer) map.remove("price");
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("employees_id",employeesId);
        qw.eq("demand_order_id",demandOrderId);
        int count = demandEmployeesService.count(qw);
        if(count>0){
            return R.failed("该员工已参与该需求单，请勿重复添加!");
        }

        DemandEmployees demandEmployees = new DemandEmployees();
        demandEmployees.setEmployeesId(employeesId);
        demandEmployees.setCreateTime(LocalDateTime.now());
        demandEmployees.setDemandOrderId(demandOrderId);
        demandEmployees.setStatus(0);
        demandEmployees.setReadStatus(0);
        demandEmployees.setPrice(price.intValue());
        demandEmployees.setUserId(TokenUtils.getCurrentUserId());
        demandEmployees.setUpdateTime(LocalDateTime.now());
        demandEmployees.setWorkTime(JSONObject.toJSONString(map.get("workDetailsPOJOS")));
        demandEmployeesService.save(demandEmployees);
        return R.ok("发送成功");
    }

    @Override
    public R newChangePrice(Map map) {
        Integer quotationId = (Integer) map.remove("quotationId");
        DemandEmployees byId1 = demandEmployeesService.getById(quotationId);
        if(this.getStatus(byId1).equals(1)){
            return R.failed("该报价单已被确认，无法修改！");
        }
        Integer price = (Integer) map.remove("price");
        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        byId.setPrice(price);
        byId.setWorkTime(JSONObject.toJSONString(map.get("workDetailsPOJOS")));
        demandEmployeesService.updateById(byId);
        return R.ok("修改成功");
    }

    @Override
    public R newSendOffer2(Map map) {
        Integer employeesId = (Integer) map.remove("employeesId");
        Integer demandOrderId = (Integer) map.remove("demandOrderId");
        Integer price = (Integer) map.remove("price");
        String attendant = (String) map.remove("attendant");
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("employees_id",employeesId);
        qw.eq("demand_order_id",demandOrderId);
        int count = demandEmployeesService.count(qw);
        if(count>0){
            return R.failed("该员工已参与该需求单，请勿重复添加!");
        }


        DemandEmployees demandEmployees = new DemandEmployees();
        demandEmployees.setEmployeesId(employeesId);
        demandEmployees.setCreateTime(LocalDateTime.now());
        demandEmployees.setDemandOrderId(demandOrderId);
        demandEmployees.setStatus(0);
        demandEmployees.setReadStatus(0);
        demandEmployees.setPrice(price.intValue());
        demandEmployees.setUserId(TokenUtils.getCurrentUserId());
        demandEmployees.setUpdateTime(LocalDateTime.now());
        demandEmployees.setWorkTime(JSONObject.toJSONString(map.get("workDetailsPOJOS")));
        demandEmployees.setAttendant(attendant);
        demandEmployeesService.save(demandEmployees);
        return R.ok("发送成功");
    }

    @Override
    public R newChangePrice2(Map map) {
        Integer quotationId = (Integer) map.remove("quotationId");
        String attendant = (String) map.remove("attendant");
        DemandEmployees byId1 = demandEmployeesService.getById(quotationId);
        if(this.getStatus(byId1).equals(1)){
            return R.failed("该报价单已被确认，无法修改！");
        }
        Integer price = (Integer) map.remove("price");
        DemandEmployees byId = demandEmployeesService.getById(quotationId);
        byId.setAttendant(attendant);
        byId.setPrice(price);
        byId.setWorkTime(JSONObject.toJSONString(map.get("workDetailsPOJOS")));
        demandEmployeesService.updateById(byId);
        return R.ok("修改成功");
    }
}
