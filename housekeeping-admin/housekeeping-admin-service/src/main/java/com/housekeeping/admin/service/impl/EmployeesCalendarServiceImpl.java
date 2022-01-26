package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.EmployeesCalendarMapper;
import com.housekeeping.admin.pojo.*;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.admin.vo.setSchedulingVO;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author su
 * @create 2020/11/12 16:22
 */
@Slf4j
@Service("employeesCalendarService")
public class EmployeesCalendarServiceImpl extends ServiceImpl<EmployeesCalendarMapper, EmployeesCalendar> implements IEmployeesCalendarService {

    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesContractService employeesContractService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private ICurrencyService currencyService;
    @Resource
    private IOrderIdService orderIdService;
    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private ICustomerAddressService customerAddressService;
    @Resource
    private ISysIndexService sysIndexService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private ISerialNumberService serialNumberService;
    @Resource
    private ISerialService serialService;
    @Resource
    private IQueryService queryService;
    @Resource
    private ISysJobNoteService sysJobNoteService;
    @Resource
    private ISysConfigService configService;
    @Resource
    private ICompanyCalendarService companyCalendarService;
    @Resource
    private IEmployeesPriceAdjustmentService employeesPriceAdjustmentService;
    @Override
    public R setCalendar2(SetEmployeesCalendar2DTO dto) {
        log.info("后台接收到的数据："+dto.toString());

        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentD(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "排班時間衝突");
        }

        /* 员工存在性判断 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN) || roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            if (!employeesDetailsService.judgmentOfExistence(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            if (!employeesDetailsService.judgmentOfExistenceFromCompany(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            if (!employeesDetailsService.judgmentOfExistenceHaveJurisdictionOverManager(dto.getEmployeesId())) return R.failed(null, "該員工不存在或不受您管辖");
        }

        /* 添加新的 */
        StringBuilder week = new StringBuilder();
        dto.getWeek().forEach(wk->{
            week.append(wk);
        });


        dto.getTimeSlotPriceDTOList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            true,
                            null,
                            week.toString(),
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            employeesCalendar.setType(timeSlot.getType());
            employeesCalendar.setPercentage(timeSlot.getPercentage());
            employeesCalendar.setHourlyWage(new BigDecimal(timeSlot.getPrice()));
            employeesCalendar.setCode(timeSlot.getCode());
            log.info("插入数据库前："+employeesCalendar.toString());
            baseMapper.insert(employeesCalendar);
        });

        return R.ok("設置成功");
    }

    @Override
    public R updateCalendar2(UpdateEmployeesCalendarDTO dto) {
        if(dto.getId()==null){
            return R.failed("請選擇某一條排班！");
        }
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentE(dto);
        if (!res.isEmpty()) return R.failed(res, "排班時間衝突");

        /* 修改 */
        EmployeesCalendar ec = this.getById(dto.getId());
        StringBuilder week = new StringBuilder();
        dto.getWeeks().forEach(wk->{
            week.append(wk);
        });
        ec.setWeek(week.toString());
        ec.setTimeSlotStart(dto.getTimeSlotStart());
        ec.setTimeSlotLength(dto.getTimeSlotLength());
        ec.setType(dto.getType());
        ec.setPercentage(dto.getPercentage());
        ec.setHourlyWage(BigDecimal.valueOf(dto.getPrice()));
        this.updateById(ec);
        return R.ok(null, "修改成功");
    }

    @Override
    public R del(Integer id) {
        this.removeById(id);
        return R.ok(null, "刪除成功");
    }

    @Override
    public R setJobs(SetEmployeesJobsDTO dto) {

        /* 员工存在性判断 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN) || roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            if (!employeesDetailsService.judgmentOfExistence(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            if (!employeesDetailsService.judgmentOfExistenceFromCompany(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            if (!employeesDetailsService.judgmentOfExistenceHaveJurisdictionOverManager(dto.getEmployeesId())) return R.failed(null, "該員工不存在或不受您管辖");
        }

        /* 先设置到预设工作内容字段 */
        StringBuilder sb = new StringBuilder();
        dto.getJobIds().forEach(jobId->{
            sb.append(jobId);
            sb.append(" ");
        });
        String jobIdsString = new String(sb).trim();
        employeesDetailsService.setPresetJobIds(jobIdsString, dto.getEmployeesId());

        return R.ok(null, "已設置預設鐘點工工作內容");
    }

    @Override
    public R getSkillTags(Integer employeesId) {
        String jobStr = employeesDetailsService.getById(employeesId).getPresetJobIds();
        if(CommonUtils.isEmpty(jobStr)) return R.ok(new ArrayList<>(), "該保潔員沒有設置可工作內容");
        String[] jobs = jobStr.split(" ");
        List<Integer> jobIds = new ArrayList<>();
        for (int i = 0; i < jobs.length; i++) {
            jobIds.add(Integer.valueOf(jobs[i]));
        }
        List<SysJobContend> sysJobContends = sysJobContendService.listByIds(jobIds);
        /***
         * select c.id, c.contend
         *         from employees_calendar as a, employees_calendar_details as b, sys_job_contend as c
         *         where a.id = b.calendar_id and b.job_id = c.id and employees_id = #{employeesId}
         *         group by job_id;
         */
        return R.ok(sysJobContends,"獲取成功");
    }

    @Override
    public List<FreeDateTimeDTO> getFreeTimeByDateSlot2(GetCalendarByDateSlotDTO dto) {
        /* 2021-2-4 暂时先这样写着，目前还没做派任务，所以空闲时间=时间表 */
        return this.getCalendarByDateSlot2(dto);
    }

    public List<FreeDateTimePriceDTO> getFreeTimeByDateSlot3(GetCalendarByDateSlotDTO dto,Integer jobId) {
        /* 空闲时间=排班时间表-订单时间 */
        List<FreeDateTimePriceDTO> calendarByDateSlot3 = this.getCalendarByDateSlot3(dto, jobId);

        return this.getFreeTime(calendarByDateSlot3,dto);
    }


    @Override
    public List<FreeDateTimeDTO> getFreeTimeByDateSlot4(GetCalendarByDateSlotDTO dto) {
        /* 空闲时间=时间表-订单时间 */
        List<FreeDateTimeDTO> calendarByDateSlot2 = this.getCalendarByDateSlot2(dto);
        return this.getFreeTime2(calendarByDateSlot2,dto);
    }

    @Override
    public R MakeAnAppointmentByDateDTO(MakeAnAppointmentByDateDTO dto) {
        if(CollectionUtils.isEmpty(dto.getJobIds())){
            return R.failed("請選擇工作內容!");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();

        /* 订单来源 *//*
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_CALENDAR);

        *//* 订单编号 *//*
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        *//* 流水号 *//*
        String serialNumber = serialNumberService.generateSerialNumber(number);
        odp.setSerialNumber(serialNumber);

        *//* 消费项目 *//*
        odp.setConsumptionItems("1");*/

        /* 订单甲方 保洁员 */
        Boolean exist = employeesDetailsService.judgmentOfExistence(dto.getEmployeesId());
        if (!exist) return R.failed(null, "保潔員不存在");
        EmployeesDetails ed = employeesDetailsService.getById(dto.getEmployeesId());
        odp.setEmployeesId(ed.getId());
        odp.setName1(ed.getName());
        odp.setPhPrefix1(ed.getPhonePrefix());
        odp.setPhone1(ed.getPhone());

        /* 甲方所属公司 */
        if(ed.getCompanyId()!=null){
            CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
            odp.setCompanyId(cod.getId().toString());
            odp.setInvoiceName(cod.getInvoiceName());
            odp.setInvoiceNumber(cod.getInvoiceNumber());
        }

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        CustomerAddress ca = customerAddressService.getById(dto.getAddressId());
        odp.setCustomerId(cd.getId());
        odp.setName2(ca.getName());
        odp.setPhPrefix2(ca.getPhonePrefix());
        odp.setPhone2(ca.getPhone());

        /* 订单工作内容 */
        String jobIds = CommonUtils.arrToString(dto.getJobIds().toArray(new Integer[0]));
        odp.setJobIds(jobIds);

        /* 订单工作筆記 */
        if(CollectionUtils.isEmpty(dto.getNotes())){
            odp.setNoteIds(null);
        }else {
            String noteIds = CommonUtils.arrToString(dto.getNotes().toArray(new Integer[0]));
            odp.setNoteIds(noteIds);
        }


        /* 地址 */
        odp.setAddress(ca.getAddress());
        odp.setLat(new Float(ca.getLat()));
        odp.setLng(new Float(ca.getLng()));


        /* 工作时间安排 */
        List<WorkDetailsPOJO> wds = this.makeAnAppointmentHandleByDate(dto, true);
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = this.days(wds);
        odp.setDays(days);

        /* 每日工作时长计算 */
        Float h = this.hOfDay(dto);
        odp.setHOfDay(h);

        /* 原价格计算 */
        BigDecimal pdb = this.totalPrice(wds);
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

        /* 订单状态 */
        odp.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);//待支付状态

        /* 订单生成时间 */
        odp.setStartDateTime(now);
        odp.setUpdateDateTime(now);

        /*int hourly;
         *//* 订单截止付款时间 保留时间 *//*
        if(ed.getCompanyId()!=null){
            hourly = orderDetailsService.orderRetentionTime(dto.getEmployeesId());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }else{
            QueryWrapper qw4 = new QueryWrapper();
            qw4.eq("config_key", ApplicationConfigConstants.orderRetentionTime);
            SysConfig config = configService.getOne(qw4);
            hourly = Integer.parseInt(config.getConfigValue());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }*/

        ConfirmOrderPOJO confirmOrderPOJO = new ConfirmOrderPOJO(odp);
        List<Integer> noteIds = CommonUtils.stringToList(confirmOrderPOJO.getNoteIds());
        if(CollectionUtils.isNotEmpty(noteIds)){
            List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
            confirmOrderPOJO.setNotes(sysJobNotes);
        }
        return R.ok(confirmOrderPOJO);
    }

    @Override
    public List<WorkDetailsPOJO> makeAnAppointmentHandleByDate(MakeAnAppointmentByDateDTO dto, boolean need) {

        String jobId = CommonUtils.arrToString(dto.getJobIds().toArray(new Integer[0]));

        List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
        /* 获取这段日期内的空闲时间 */
        List<FreeDateTimeDTO> freeTime = this.getFreeTimeByDateSlot5(new GetCalendarByDateSlotDTO(new DateSlot(dto.getStart(), dto.getEnd()), dto.getEmployeesId()),jobId);
        freeTime.forEach(x -> {
            /* 今日数据准备 */
            LocalDate today = x.getDate(); //当日日期
            Integer todayWeek = today.getDayOfWeek().getValue();
            if(!dto.getDates().contains(today))return;     //如果预约日期没有这天，那么就跳过吧
            List<LocalTimeAndPricePOJO> enableTimeToday = this.enableTimeToday(x.getTimes());//切割的时间表与价格
            List<LocalTime> item = sysIndexService.periodSplittingB(dto.getTimeSlots());//切割的需求时间段
            Boolean todayIsOk = this.judgeToday(enableTimeToday, item);         //判断今天行不行
            List<TimeSlot> timeSlots = new ArrayList<>();
            if (need) {
                /*if(todayIsOk) timeSlots = this.withPriceOfSlot(item, enableTimeToday);*/
                if(todayIsOk) timeSlots = this.withPriceOfSlot2(item, enableTimeToday,dto.getEmployeesId(),dto.getJobIds().get(0));//给时段加价格
                else timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }else {
                timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }

            Boolean canBeOnDuty = todayIsOk;
            BigDecimal todayPrice = new BigDecimal(0);
            /*if (canBeOnDuty) todayPrice = this.todayPrice(enableTimeToday, item);*/
            if (canBeOnDuty) todayPrice = this.todayPrice2(timeSlots);

            /* 今日数据返回 */
            WorkDetailsPOJO wdp = new WorkDetailsPOJO(today, todayWeek, timeSlots, canBeOnDuty, todayPrice);
            workDetailsPOJOS.add(wdp);
        });
        return workDetailsPOJOS;
    }

    @Override
    public R confirmOrderByDate(MakeAnAppointmentByDateDTO dto) {
        if(CollectionUtils.isEmpty(dto.getJobIds())){
            return R.failed("請選擇工作內容!");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();

        /* 订单来源 */
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_CALENDAR);

        /* 订单编号 */
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        /* 流水号 */
        String serialNumber = serialNumberService.generateSerialNumber(number);
        odp.setSerialNumber(serialNumber);

        /* 消费项目 */
        odp.setConsumptionItems("1");

        /* 订单甲方 保洁员 */
        Boolean exist = employeesDetailsService.judgmentOfExistence(dto.getEmployeesId());
        if (!exist) return R.failed(null, "保潔員不存在");
        EmployeesDetails ed = employeesDetailsService.getById(dto.getEmployeesId());
        odp.setEmployeesId(ed.getId());
        odp.setName1(ed.getName());
        odp.setPhPrefix1(ed.getPhonePrefix());
        odp.setPhone1(ed.getPhone());

        /* 甲方所属公司 */
        if(ed.getCompanyId()!=null){
            CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
            odp.setCompanyId(cod.getId().toString());
            odp.setInvoiceName(cod.getInvoiceName());
            odp.setInvoiceNumber(cod.getInvoiceNumber());
        }

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        CustomerAddress ca = customerAddressService.getById(dto.getAddressId());
        odp.setCustomerId(cd.getId());
        odp.setName2(ca.getName());
        odp.setPhPrefix2(ca.getPhonePrefix());
        odp.setPhone2(ca.getPhone());

        /* 订单工作内容 */
        String jobIds = CommonUtils.arrToString(dto.getJobIds().toArray(new Integer[0]));
        odp.setJobIds(jobIds);

        /* 订单工作筆記 */
        if(CollectionUtils.isEmpty(dto.getNotes())){
            odp.setNoteIds(null);
        }else {
            String noteIds = CommonUtils.arrToString(dto.getNotes().toArray(new Integer[0]));
            odp.setNoteIds(noteIds);
        }


        /* 地址 */
        odp.setAddress(ca.getAddress());
        odp.setLat(new Float(ca.getLat()));
        odp.setLng(new Float(ca.getLng()));


        /* 工作时间安排 */
        List<WorkDetailsPOJO> wds = this.makeAnAppointmentHandleByDate(dto, true);
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = this.days(wds);
        odp.setDays(days);

        /* 每日工作时长计算 */
        Float h = this.hOfDay(dto);
        odp.setHOfDay(h);

        /* 原价格计算 */
        BigDecimal pdb = this.totalPrice(wds);
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

        /* 订单状态 */
        odp.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);//待支付状态

        /* 订单生成时间 */
        odp.setStartDateTime(now);
        odp.setUpdateDateTime(now);

        int hourly;
        /* 订单截止付款时间 保留时间 */
        if(ed.getCompanyId()!=null){
            hourly = orderDetailsService.orderRetentionTime(dto.getEmployeesId());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }else{
            QueryWrapper qw6 = new QueryWrapper();
            qw6.eq("config_key", ApplicationConfigConstants.orderRetentionTime);
            SysConfig config = configService.getOne(qw6);
            hourly = Integer.parseInt(config.getConfigValue());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }

        String key = "OrderToBePaid:employeesId"+dto.getEmployeesId()+":" + number;
        Map<String, Object> map = new HashMap<>();
        try {
            map = CommonUtils.objectToMap(odp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, hourly, TimeUnit.HOURS);
        serialService.generatePipeline(odp);

        ConfirmOrderPOJO confirmOrderPOJO = new ConfirmOrderPOJO(odp);
        List<Integer> noteIds = CommonUtils.stringToList(confirmOrderPOJO.getNoteIds());
        if(CollectionUtils.isNotEmpty(noteIds)){
            List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
            confirmOrderPOJO.setNotes(sysJobNotes);
        }
        return R.ok(confirmOrderPOJO,"预约成功");
    }

    private List<FreeDateTimeDTO> getFreeTime2(List<FreeDateTimeDTO> calendarByDateSlot, GetCalendarByDateSlotDTO dto) {
        //获取所有订单
        List<OrderDetailsPOJO> orderByEmpId = orderDetailsService.getOrderByEmpId(dto.getId());

        List<FreeDateTimeDTO> hasWork = new ArrayList<>();
        LocalDate start = dto.getDateSlot().getStart();
        LocalDate end = dto.getDateSlot().getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            LocalDate finalDate = date;
            FreeDateTimeDTO freeDateTimeDTO = new FreeDateTimeDTO();
            freeDateTimeDTO.setDate(finalDate);
            freeDateTimeDTO.setWeek(date.getDayOfWeek().getValue());
            List<TimeSlotPOJO> timeSlotPricePOJOS = new ArrayList<>();
            orderByEmpId.forEach(x ->{
                List<WorkDetailsPOJO> workDetails = x.getWorkDetails();
                workDetails.forEach(y ->{
                    LocalDate date1 = y.getDate();
                    if(date1.equals(finalDate)){
                        List<TimeSlot> timeSlots = y.getTimeSlots();
                        timeSlots.forEach(z ->{
                            TimeSlotPOJO timeSlotPricePOJO = new TimeSlotPOJO();
                            timeSlotPricePOJO.setTimeSlotStart(z.getTimeSlotStart());
                            timeSlotPricePOJO.setTimeSlotLength(z.getTimeSlotLength());
                            timeSlotPricePOJOS.add(timeSlotPricePOJO);
                        });
                    }
                });
            });
            freeDateTimeDTO.setTimes(timeSlotPricePOJOS);
            hasWork.add(freeDateTimeDTO);
        }

        for (int i = 0; i < calendarByDateSlot.size(); i++) {

            FreeDateTimeDTO freeDateTimeDTO = calendarByDateSlot.get(i);
            List<TimeSlotPOJO> times = freeDateTimeDTO.getTimes();

            FreeDateTimeDTO freeDateTimePriceDTO1 = hasWork.get(i);
            List<TimeSlotPOJO> times1 = freeDateTimePriceDTO1.getTimes();


            times.forEach(x ->{
                ArrayList<TimeSlotPOJO> timeSlotPricePOJOS = new ArrayList<>();
                LocalTime timeSlotStart = x.getTimeSlotStart();
                LocalTime timeSlotEnd = timeSlotStart.plusMinutes((long) ((x.getTimeSlotLength()/0.5)*30));
                List<String> intervalTimeList = TimeUtils.getIntervalTimeList(timeSlotStart.toString(), timeSlotEnd.toString(), 30);
                for (int i1 = 0; i1 < intervalTimeList.size()-1; i1++) {
                    TimeSlotPOJO timeSlotPricePOJO = new TimeSlotPOJO();
                    timeSlotPricePOJO.setTimeSlotStart(LocalTime.parse(intervalTimeList.get(i1), DateTimeFormatter.ofPattern("HH:mm")));
                    timeSlotPricePOJO.setTimeSlotLength((float) 0.5);
                    timeSlotPricePOJO.setCode(x.getCode());
                    timeSlotPricePOJO.setType(x.getType());
                    timeSlotPricePOJO.setPercentage(x.getPercentage());
                    timeSlotPricePOJO.setHourlyWage(x.getHourlyWage());
                    timeSlotPricePOJOS.add(timeSlotPricePOJO);
                }

                ArrayList<TimeSlotPOJO> timeSlotPricePOJOS2 = new ArrayList<>();
                times1.forEach(y ->{
                    LocalTime timeSlotStart2 = y.getTimeSlotStart();
                    LocalTime timeSlotEnd2 = timeSlotStart2.plusMinutes((long) ((y.getTimeSlotLength()/0.5)*30));
                    List<String> intervalTimeList2 = TimeUtils.getIntervalTimeList(timeSlotStart2.toString(), timeSlotEnd2.toString(), 30);
                    for (int i1 = 0; i1 < intervalTimeList2.size()-1; i1++) {
                        TimeSlotPOJO timeSlotPricePOJO = new TimeSlotPOJO();
                        timeSlotPricePOJO.setTimeSlotStart(LocalTime.parse(intervalTimeList2.get(i1), DateTimeFormatter.ofPattern("HH:mm")));
                        timeSlotPricePOJO.setTimeSlotLength((float) 0.5);
                        timeSlotPricePOJO.setCode(x.getCode());
                        timeSlotPricePOJO.setType(x.getType());
                        timeSlotPricePOJO.setPercentage(x.getPercentage());
                        timeSlotPricePOJO.setHourlyWage(x.getHourlyWage());
                        timeSlotPricePOJOS2.add(timeSlotPricePOJO);
                    }
                });
                timeSlotPricePOJOS.removeAll(timeSlotPricePOJOS2);
                freeDateTimeDTO.setTimes(timeSlotPricePOJOS);
            });
        }

        calendarByDateSlot.forEach(x ->{
            List<TimeSlotPOJO> item = x.getTimes();

            /* 带价格的时间段生成 */
            List<TimeSlotPOJO> timeSlots = new ArrayList<>();
            LocalTime start2 = LocalTime.MIN; //当前段的开始时间
            Float length = 0f;//当前段累计长度
            LocalTime lastTime = LocalTime.MIN; //上一个遍历到的时间记录

            for (TimeSlotPOJO time : item) {
                //生成新段的条件 价格跳段 或 时间段跳段
                LocalTime b = time.getTimeSlotStart();  //当前半小时

                //生成新段判断  价格跳段 或 时间段跳段
                if (!lastTime.plusMinutes(30).equals(b)){
                    //结束上一段,将上一段放进结果中
                    if (length!=0) {
                        TimeSlotPOJO timeSlot = new TimeSlotPOJO();
                        timeSlot.setTimeSlotStart(start2);
                        timeSlot.setTimeSlotLength(length);
                        timeSlot.setCode(time.getCode());
                        timeSlot.setType(time.getType());
                        timeSlot.setPercentage(time.getPercentage());
                        timeSlot.setHourlyWage(time.getHourlyWage());
                        timeSlots.add(timeSlot);
                    }
                    //开始新段
                    start2 = b; length = 0f;
                }

                //老段延长
                length += 0.5f;

                //最后必做的事~更新这两个值
                lastTime = b;
            }

            //最后还存留一段时间段长度不小于0.5h的时间段，只需要上传就好了
            if(CollectionUtils.isNotEmpty(x.getTimes())){
                TimeSlotPOJO timeSlot = new TimeSlotPOJO();
                timeSlot.setTimeSlotStart(start2);
                timeSlot.setTimeSlotLength(length);
                String code;
                if(CollectionUtils.isNotEmpty(x.getTimes())){
                    code = x.getTimes().get(x.getTimes().size() - 1).getCode();
                }else{
                    code = null;
                }
                timeSlot.setCode(code);
                BigDecimal hourlyWage;
                if(CollectionUtils.isNotEmpty(x.getTimes())){
                    hourlyWage = x.getTimes().get(x.getTimes().size() - 1).getHourlyWage();
                }else{
                    hourlyWage = null;
                }
                timeSlot.setHourlyWage(hourlyWage);

                Integer type;
                if(CollectionUtils.isNotEmpty(x.getTimes())){
                    type = x.getTimes().get(x.getTimes().size() - 1).getType();
                }else{
                    type = null;
                }
                timeSlot.setType(type);

                Integer percentage;
                if(CollectionUtils.isNotEmpty(x.getTimes())){
                    percentage = x.getTimes().get(x.getTimes().size() - 1).getPercentage();
                }else{
                    percentage = null;
                }
                timeSlot.setPercentage(percentage);

                timeSlots.add(timeSlot);
            }
            x.setTimes(timeSlots);
            if(CollectionUtils.isEmpty(x.getTimes())){
                x.setHasTime(false);
            }
        });

        return calendarByDateSlot;
    }


    public List<FreeDateTimePriceDTO> getCalendarByDateSlot3(GetCalendarByDateSlotDTO dto, Integer jobId) {

        Integer empId = dto.getId();
        EmployeesDetails byId = employeesDetailsService.getById(empId);
        List<String> skills = Arrays.asList(byId.getPresetJobIds().split(" "));
        List<String> price = Arrays.asList(byId.getJobPrice().split(" "));
        int i1 = skills.indexOf(jobId.toString());
        BigDecimal workPrice = new BigDecimal(price.get(i1));

        List<FreeDateTimePriceDTO> freeDateTimeDTOS = new ArrayList<>();

        /* 先得到三大map */
        SortListUtil<TimeSlotPricePOJO> sort = new SortListUtil<>();
        Map<LocalDate, List<TimeSlotPricePOJO>> map1 = new HashMap<>();
        Map<Integer, List<TimeSlotPricePOJO>> map2 = new HashMap<>();
        Map<String, List<TimeSlotPricePOJO>> map3 = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", dto.getId());
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);

        employeesCalendarList.forEach(ec -> {
            TimeSlotPricePOJO pojo = new TimeSlotPricePOJO();
            pojo.setTimeSlotStart(ec.getTimeSlotStart());
            pojo.setTimeSlotLength(ec.getTimeSlotLength());
            pojo.setCode(ec.getCode());
            if(ec.getType()==null||ec.getType().equals(0)){
                pojo.setHourlyWage(ec.getHourlyWage());
                pojo.setTotalPrice(workPrice.add(ec.getHourlyWage()));
            }else{
                if(ec.getType().equals(1)){
                    BigDecimal divide = ((BigDecimal.valueOf(ec.getPercentage())).divide(BigDecimal.valueOf(100)));
                    BigDecimal bigDecimal = workPrice.multiply(divide).setScale(0,BigDecimal.ROUND_UP);
                    pojo.setHourlyWage(bigDecimal);
                    pojo.setTotalPrice(workPrice.add(bigDecimal));
                }
            }

            if (CommonUtils.isEmpty(ec.getStander())){
                List<TimeSlotPricePOJO> pojoList = map3.getOrDefault("", new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map3.put("", pojoList);
            }else if (ec.getStander() == false){ //日期
                List<TimeSlotPricePOJO> pojoList = map1.getOrDefault(ec.getDate(), new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map1.put(ec.getDate(), pojoList);
            }else if (ec.getStander() == true){ //周
                String weekString = ec.getWeek();
                for (int i = 0; i < weekString.length(); i++) {
                    Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                    List<TimeSlotPricePOJO> pojoList = map2.getOrDefault(weekInteger, new ArrayList<>());
                    pojoList.add(pojo);
                    sort.Sort(pojoList, "getTimeSlotStart", null);
                    map2.put(weekInteger, pojoList);
                }
            }
        });
        /* 先得到三大map */


        LocalDate start = dto.getDateSlot().getStart();
        LocalDate end = dto.getDateSlot().getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            FreeDateTimePriceDTO freeDateTimeDTO = new FreeDateTimePriceDTO();
            List<TimeSlotPricePOJO> todaySlot = new ArrayList<>();
            if (map1.containsKey(date)){
                //日期模板生效
                todaySlot = map1.get(date);
            }else if (!map2.isEmpty()){
                //周模板生效
                todaySlot = map2.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>());
            }else if (!map3.isEmpty()){
                //通用模板生效
                todaySlot = map3.get("");
            }
            if (todaySlot.size() != 0){
                freeDateTimeDTO.setDate(date);
                freeDateTimeDTO.setTimes(todaySlot);
                freeDateTimeDTO.setHasTime(true);
            }else {
                freeDateTimeDTO.setDate(date);
                freeDateTimeDTO.setTimes(new ArrayList<>());
                freeDateTimeDTO.setHasTime(false);
            }
            freeDateTimeDTO.setWeek(date.getDayOfWeek().getValue());
            freeDateTimeDTOS.add(freeDateTimeDTO);
        }

         for (FreeDateTimePriceDTO x : freeDateTimeDTOS) {
            LocalDate date = x.getDate();
            QueryWrapper<EmployeesPriceAdjustment> qw2 = new QueryWrapper<>();
            qw2.eq("employees_id", empId);
            qw2.like("date", date.toString());
            qw2.like("job_ids", jobId);
            EmployeesPriceAdjustment one = employeesPriceAdjustmentService.getOne(qw2);
            if (one != null) {
                //收费开关
                if(true == one.getStatus()){
                    List<TimeSlotPricePOJO> times = x.getTimes();
                    List<TimeSlotPricePOJO> collect = times.stream().map(y -> {
                        TimeSlotPricePOJO timeSlotPricePOJO = new TimeSlotPricePOJO(y);
                        if (one.getType() == null || one.getType().equals(0)) {
                            timeSlotPricePOJO.setHourlyWage(timeSlotPricePOJO.getHourlyWage().add(one.getHourlyWage()));
                            timeSlotPricePOJO.setTotalPrice(timeSlotPricePOJO.getTotalPrice().add(one.getHourlyWage()));
                        } else if (one.getType().equals(1)) {
                            BigDecimal divide = ((BigDecimal.valueOf(one.getPercentage())).divide(BigDecimal.valueOf(100)));
                            BigDecimal add = timeSlotPricePOJO.getHourlyWage().add(workPrice);
                            BigDecimal bigDecimal = add.multiply(divide).setScale(0, BigDecimal.ROUND_UP);
                            timeSlotPricePOJO.setHourlyWage(timeSlotPricePOJO.getHourlyWage().add(bigDecimal));
                            timeSlotPricePOJO.setTotalPrice(timeSlotPricePOJO.getTotalPrice().add(bigDecimal));
                        }
                        return timeSlotPricePOJO;
                    }).collect(Collectors.toList());
                    x.setTimes(collect);
                }
            }
        }

        return freeDateTimeDTOS;
    }

    @Override
    public R getFreeTimeByMonth(GetFreeTimeByMonthDTO dto) {
        if (dto.getMonth()>12 || dto.getMonth()<1) return R.failed(null, "月份錯誤");
        if (dto.getYear() < LocalDate.now().getYear()) return R.failed(null, "年份不能选择以前");

        LocalDate thisMonthFirstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
                                                                              //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
                                                                       //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);
        List<FreeDateTimeDTO> freeTime =  getFreeTimeByDateSlot4(new GetCalendarByDateSlotDTO(new DateSlot(startDate, endDate), dto.getEmployeesId()));
        List<CalendarInfoDTO> list = freeTime.stream().map(x -> {
            CalendarInfoDTO calendarInfoDTO = new CalendarInfoDTO(x);
            calendarInfoDTO.setIsThisMonth(x.getDate().getMonth().getValue() == dto.getMonth());
            calendarInfoDTO.setDay(x.getDate().toString().substring(8));
            calendarInfoDTO.setIsThisDay(x.getDate().equals(LocalDate.now()));
            calendarInfoDTO.setIsAfter(x.getDate().isAfter(LocalDate.now())||x.getDate().equals(LocalDate.now()));
            return calendarInfoDTO;
        }).collect(Collectors.toList());

        return R.ok(list,"獲取成功");
    }

    @Override
    public R getAbsenceDaysByMonth(GetFreeTimeByMonthDTO dto) {
        if (dto.getMonth()>12 || dto.getMonth()<1) return R.failed(null, "月份錯誤");
        if (dto.getYear() < LocalDate.now().getYear()) return R.failed(null, "年份不能选择以前");

        LocalDate thisMonthFirstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
        //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
        //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);
        List<FreeDateTimeDTO> freeTime =  getFreeTimeByDateSlot2(new GetCalendarByDateSlotDTO(new DateSlot(startDate, endDate), dto.getEmployeesId()));
        List<LocalDate> list = freeTime.stream().map(x -> {
            if (!x.getHasTime()) return x.getDate();
            else return null;
        }).collect(Collectors.toList());
        list.removeIf(x -> x == null);
        return R.ok(list,"獲取成功");
    }

    @Override
    public R getAbsenceDaysByDateSlot(GetCalendarByDateSlotDTO dto) {
        LocalDate startDate = dto.getDateSlot().getStart();
        LocalDate endDate = dto.getDateSlot().getEnd();
        Integer employeesId = dto.getId();
        List<FreeDateTimeDTO> freeTime =  getFreeTimeByDateSlot2(new GetCalendarByDateSlotDTO(new DateSlot(startDate, endDate), employeesId));
        List<LocalDate> list = freeTime.stream().map(x -> {
            if (!x.getHasTime()) return x.getDate();
            else return null;
        }).collect(Collectors.toList());
        list.removeIf(x -> x == null);
        return R.ok(list,"獲取成功");
    }

    @Override
    public R makeAnAppointment(MakeAnAppointmentDTO dto) {

        if(CollectionUtils.isEmpty(dto.getJobIds())){
            return R.failed("請選擇工作內容!");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();

        /* 订单来源 *//*
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_CALENDAR);

        *//* 订单编号 *//*
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        *//* 流水号 *//*
        String serialNumber = serialNumberService.generateSerialNumber(number);
        odp.setSerialNumber(serialNumber);

        *//* 消费项目 *//*
        odp.setConsumptionItems("1");*/

        /* 订单甲方 保洁员 */
        Boolean exist = employeesDetailsService.judgmentOfExistence(dto.getEmployeesId());
        if (!exist) return R.failed(null, "保潔員不存在");
        EmployeesDetails ed = employeesDetailsService.getById(dto.getEmployeesId());
        odp.setEmployeesId(ed.getId());
        odp.setName1(ed.getName());
        odp.setPhPrefix1(ed.getPhonePrefix());
        odp.setPhone1(ed.getPhone());

        /* 甲方所属公司 */
        if(ed.getCompanyId()!=null){
            CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
            odp.setCompanyId(cod.getId().toString());
            odp.setInvoiceName(cod.getInvoiceName());
            odp.setInvoiceNumber(cod.getInvoiceNumber());
        }

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        CustomerAddress ca = customerAddressService.getById(dto.getAddressId());
        odp.setCustomerId(cd.getId());
        odp.setName2(ca.getName());
        odp.setPhPrefix2(ca.getPhonePrefix());
        odp.setPhone2(ca.getPhone());

        /* 订单工作内容 */
        String jobIds = CommonUtils.arrToString(dto.getJobIds().toArray(new Integer[0]));
        odp.setJobIds(jobIds);

        /* 订单工作筆記 */
        if(CollectionUtils.isEmpty(dto.getNotes())){
            odp.setNoteIds(null);
        }else {
            String noteIds = CommonUtils.arrToString(dto.getNotes().toArray(new Integer[0]));
            odp.setNoteIds(noteIds);
        }


        /* 地址 */
        odp.setAddress(ca.getAddress());
        odp.setLat(new Float(ca.getLat()));
        odp.setLng(new Float(ca.getLng()));


        /* 工作时间安排 */
        List<WorkDetailsPOJO> wds = this.makeAnAppointmentHandle(dto, true);
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = this.days(wds);
        odp.setDays(days);

        /* 每日工作时长计算 */
        Float h = this.hOfDay(dto);
        odp.setHOfDay(h);

        /* 原价格计算 */
        BigDecimal pdb = this.totalPrice(wds);
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

        /* 订单状态 */
        odp.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);//待支付状态

        /* 订单生成时间 */
        odp.setStartDateTime(now);
        odp.setUpdateDateTime(now);

        /*int hourly;
        *//* 订单截止付款时间 保留时间 *//*
        if(ed.getCompanyId()!=null){
            hourly = orderDetailsService.orderRetentionTime(dto.getEmployeesId());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }else{
            QueryWrapper qw4 = new QueryWrapper();
            qw4.eq("config_key", ApplicationConfigConstants.orderRetentionTime);
            SysConfig config = configService.getOne(qw4);
            hourly = Integer.parseInt(config.getConfigValue());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }*/

        ConfirmOrderPOJO confirmOrderPOJO = new ConfirmOrderPOJO(odp);
        List<Integer> noteIds = CommonUtils.stringToList(confirmOrderPOJO.getNoteIds());
        if(CollectionUtils.isNotEmpty(noteIds)){
            List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
            confirmOrderPOJO.setNotes(sysJobNotes);
        }
        return R.ok(confirmOrderPOJO);
    }

    @Override
    public R getByCalendarId(Integer id) {
        /* 數據從數據庫獲取 */
        EmployeesCalendar ec = employeesCalendarService.getById(id);

        /* 返回信息 */
        return R.ok(ec, "獲取成功");
    }

    @Override
    public Boolean judgmentOfExistenceByEmployeesId(Integer employeesId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        List<EmployeesCalendar> re = employeesCalendarService.list(qw);
        if (re.isEmpty()) return false;
        return true;
    }

    @Override
    public List<WorkDetailsPOJO> makeAnAppointmentHandle(MakeAnAppointmentDTO dto, Boolean need) {

        String jobId = CommonUtils.arrToString(dto.getJobIds().toArray(new Integer[0]));

        List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
        /* 获取这段日期内的空闲时间 */
        List<FreeDateTimeDTO> freeTime = this.getFreeTimeByDateSlot5(new GetCalendarByDateSlotDTO(new DateSlot(dto.getStart(), dto.getEnd()), dto.getEmployeesId()),jobId);
        freeTime.forEach(x -> {
            /* 今日数据准备 */
            LocalDate today = x.getDate(); //今日日期
            Integer todayWeek = today.getDayOfWeek().getValue();
            if (!dto.getWeeks().contains(todayWeek)) return;     //如果周数没有这天，那么就跳过吧
            List<LocalTimeAndPricePOJO> enableTimeToday = this.enableTimeToday(x.getTimes());//切割的时间表与价格
            List<LocalTime> item = sysIndexService.periodSplittingB(dto.getTimeSlots());//切割的需求时间段
            Boolean todayIsOk = this.judgeToday(enableTimeToday, item);         //判断今天行不行
            List<TimeSlot> timeSlots = new ArrayList<>();
            if (need) {
                /*if(todayIsOk) timeSlots = this.withPriceOfSlot(item, enableTimeToday);*/
                if(todayIsOk) timeSlots = this.withPriceOfSlot2(item, enableTimeToday,dto.getEmployeesId(),dto.getJobIds().get(0));//给时段加价格
                else timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }else {
                timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }

            Boolean canBeOnDuty = todayIsOk;
            BigDecimal todayPrice = new BigDecimal(0);
            /*if (canBeOnDuty) todayPrice = this.todayPrice(enableTimeToday, item);*/
            if (canBeOnDuty) todayPrice = this.todayPrice2(timeSlots);

            /* 今日数据返回 */
            WorkDetailsPOJO wdp = new WorkDetailsPOJO(today, todayWeek, timeSlots, canBeOnDuty, todayPrice);
            workDetailsPOJOS.add(wdp);
        });
        return workDetailsPOJOS;
    }

    private List<FreeDateTimeDTO> getFreeTimeByDateSlot5(GetCalendarByDateSlotDTO dto,String jobId) {
        /* 空闲时间=时间表-订单时间 */
        List<FreeDateTimeDTO> calendarByDateSlot2 = this.getCalendarByDateSlot4(dto,jobId);
        return this.getFreeTime2(calendarByDateSlot2,dto);
    }

    private List<FreeDateTimeDTO> getCalendarByDateSlot4(GetCalendarByDateSlotDTO dto, String jobId) {

        EmployeesDetails byId = employeesDetailsService.getById(dto.getId());
        List<String> skills = Arrays.asList(byId.getPresetJobIds().split(" "));
        List<String> price = Arrays.asList(byId.getJobPrice().split(" "));
        int i1 = skills.indexOf(jobId);
        BigDecimal workPrice = new BigDecimal(price.get(i1));

        List<FreeDateTimeDTO> freeDateTimeDTOS = new ArrayList<>();

        /* 先得到三大map */
        SortListUtil<TimeSlotPOJO> sort = new SortListUtil<>();
        Map<LocalDate, List<TimeSlotPOJO>> map1 = new HashMap<>();
        Map<Integer, List<TimeSlotPOJO>> map2 = new HashMap<>();
        Map<String, List<TimeSlotPOJO>> map3 = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", dto.getId());
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);

        employeesCalendarList.forEach(ec -> {
            TimeSlotPOJO pojo = new TimeSlotPOJO();
            pojo.setTimeSlotStart(ec.getTimeSlotStart());
            pojo.setTimeSlotLength(ec.getTimeSlotLength());
            pojo.setType(ec.getType());
            pojo.setPercentage(ec.getPercentage());
            pojo.setCode(ec.getCode());
            if(ec.getType()==null||ec.getType().equals(0)){
                pojo.setHourlyWage(ec.getHourlyWage());
            }else{
                if(ec.getType().equals(1)){
                    BigDecimal divide = ((BigDecimal.valueOf(ec.getPercentage())).divide(BigDecimal.valueOf(100)));
                    BigDecimal bigDecimal = workPrice.multiply(divide).setScale(0,BigDecimal.ROUND_UP);
                    pojo.setHourlyWage(bigDecimal);
                }
            }
            if (CommonUtils.isEmpty(ec.getStander())){
                List<TimeSlotPOJO> pojoList = map3.getOrDefault("", new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map3.put("", pojoList);
            }else if (ec.getStander() == false){ //日期
                List<TimeSlotPOJO> pojoList = map1.getOrDefault(ec.getDate(), new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map1.put(ec.getDate(), pojoList);
            }else if (ec.getStander() == true){ //周
                String weekString = ec.getWeek();
                for (int i = 0; i < weekString.length(); i++) {
                    Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                    List<TimeSlotPOJO> pojoList = map2.getOrDefault(weekInteger, new ArrayList<>());
                    pojoList.add(pojo);
                    sort.Sort(pojoList, "getTimeSlotStart", null);
                    map2.put(weekInteger, pojoList);
                }
            }
        });
        /* 先得到三大map */


        LocalDate start = dto.getDateSlot().getStart();
        LocalDate end = dto.getDateSlot().getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            FreeDateTimeDTO freeDateTimeDTO = new FreeDateTimeDTO();
            List<TimeSlotPOJO> todaySlot = new ArrayList<>();
            if (map1.containsKey(date)){
                //日期模板生效
                todaySlot = map1.get(date);
            }else if (!map2.isEmpty()){
                //周模板生效
                todaySlot = map2.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>());
            }else if (!map3.isEmpty()){
                //通用模板生效
                todaySlot = map3.get("");
            }
            if (todaySlot.size() != 0){
                freeDateTimeDTO.setDate(date);
                freeDateTimeDTO.setTimes(todaySlot);
                freeDateTimeDTO.setHasTime(true);
            }else {
                freeDateTimeDTO.setDate(date);
                freeDateTimeDTO.setTimes(new ArrayList<>());
                freeDateTimeDTO.setHasTime(false);
            }
            freeDateTimeDTO.setWeek(date.getDayOfWeek().getValue());
            freeDateTimeDTOS.add(freeDateTimeDTO);
        }

        for (FreeDateTimeDTO x : freeDateTimeDTOS) {
            LocalDate date = x.getDate();
            QueryWrapper<EmployeesPriceAdjustment> qw2 = new QueryWrapper<>();
            qw2.eq("employees_id", dto.getId());
            qw2.like("date", date.toString());
            qw2.like("job_ids", jobId);
            EmployeesPriceAdjustment one = employeesPriceAdjustmentService.getOne(qw2);
            if (one != null) {
                if(true == one.getStatus()){
                    List<TimeSlotPOJO> times = x.getTimes();
                    List<TimeSlotPOJO> collect = times.stream().map(y -> {
                        TimeSlotPOJO timeSlotPOJO = new TimeSlotPOJO(y);
                        if (one.getType() == null || one.getType().equals(0)) {
                            timeSlotPOJO.setHourlyWage(timeSlotPOJO.getHourlyWage().add(one.getHourlyWage()));
                        } else if (one.getType().equals(1)) {
                            BigDecimal divide = ((BigDecimal.valueOf(one.getPercentage())).divide(BigDecimal.valueOf(100)));
                            BigDecimal add = timeSlotPOJO.getHourlyWage().add(workPrice);
                            BigDecimal bigDecimal = add.multiply(divide).setScale(0, BigDecimal.ROUND_UP);
                            timeSlotPOJO.setHourlyWage(timeSlotPOJO.getHourlyWage().add(bigDecimal));
                        }
                        return timeSlotPOJO;
                    }).collect(Collectors.toList());
                    x.setTimes(collect);
                }
            }
        }

        return freeDateTimeDTOS;

    }

    public List<WorkDetailsPOJO> makeAnAppointmentHandle2(MakeAnAppointmentDTO dto, Boolean need,BigDecimal totalPrice) {
        List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
        /* 获取这段日期内的空闲时间 */
        List<FreeDateTimeDTO> freeTime = this.getFreeTimeByDateSlot4(new GetCalendarByDateSlotDTO(new DateSlot(dto.getStart(), dto.getEnd()), dto.getEmployeesId()));
        freeTime.forEach(x -> {
            /* 今日数据准备 */
            LocalDate today = x.getDate(); //今日日期
            Integer todayWeek = today.getDayOfWeek().getValue();
            if (!dto.getWeeks().contains(todayWeek)) return;     //如果周数没有这天，那么就跳过吧
            List<LocalTimeAndPricePOJO> enableTimeToday = this.enableTimeToday(x.getTimes());//切割的时间表与价格
            List<LocalTime> item = sysIndexService.periodSplittingB(dto.getTimeSlots());//切割的需求时间段
            Boolean todayIsOk = this.judgeToday(enableTimeToday, item);         //判断今天行不行
            List<TimeSlot> timeSlots = new ArrayList<>();
            if (need) {
                if(todayIsOk) timeSlots = this.withPriceOfSlot(item, enableTimeToday);
                else timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }else {
                timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }

            Boolean canBeOnDuty = todayIsOk;
            BigDecimal todayPrice = new BigDecimal(0);
            /*if (canBeOnDuty) todayPrice = this.todayPrice(enableTimeToday, item);*/
            todayPrice = totalPrice.divide(BigDecimal.valueOf(freeTime.size())).setScale(0, BigDecimal.ROUND_UP);

            /* 今日数据返回 */
            WorkDetailsPOJO wdp = new WorkDetailsPOJO(today, todayWeek, timeSlots, canBeOnDuty, todayPrice);
            workDetailsPOJOS.add(wdp);
        });
        return workDetailsPOJOS;
    }

    private BigDecimal todayPrice2(List<TimeSlot> timeSlots) {
        BigDecimal todayPrice = BigDecimal.valueOf(0);
        for (int i = 0; i < timeSlots.size(); i++) {
            todayPrice = todayPrice.add(new BigDecimal(timeSlots.get(i).getThisSlotPrice()));
        }
        return todayPrice;
    }

    private List<TimeSlot> withPriceOfSlot2(List<LocalTime> item, List<LocalTimeAndPricePOJO> enableTimeToday, Integer employeesId, Integer jobId) {
        //工作小类一小时价格
        BigDecimal bigDecimal = queryService.variablePrice(employeesId, jobId);

        /* 价格格式处理 */
        Map<LocalTime, LocalTimeAndPricePOJO> ltpMap = new HashMap<>();
        enableTimeToday.forEach(ett -> {
            ltpMap.put(ett.getTime(), ett);
        });

        /* 带价格的时间段生成 */
        List<TimeSlot> timeSlots = new ArrayList<>();
        LocalTime start = LocalTime.MIN; //当前段的开始时间
        Float length = 0f;//当前段累计长度
        BigDecimal lastHw = new BigDecimal(-1); //上一个的时薪记录
        LocalTime lastTime = LocalTime.MIN; //上一个遍历到的时间记录

        for (LocalTime time : item) {
            //生成新段的条件 价格跳段 或 时间段跳段
            LocalTimeAndPricePOJO ltp = ltpMap.get(time); //当前半小时，价格
            BigDecimal a = ltp.getHourlyWage(); //当前半小时的时薪
            LocalTime b = time;  //当前半小时

            //生成新段判断  价格跳段 或 时间段跳段
            if (!a.equals(lastHw) || !lastTime.plusMinutes(30).equals(b)){
                //结束上一段,将上一段放进结果中
                if (length!=0) {
                    TimeSlot timeSlot = new TimeSlot(start, length);
                    timeSlot.setThisSlotPrice(((lastHw.add(bigDecimal)).multiply(new BigDecimal(length))).setScale(0, BigDecimal.ROUND_DOWN).toString());
                    timeSlots.add(timeSlot);
                }
                //开始新段
                start = b; length = 0f;
            }

            //老段延长
            length += 0.5f;

            //最后必做的事~更新这两个值
            lastHw = a; lastTime = b;
        }

        //最后还存留一段时间段长度不小于0.5h的时间段，只需要上传就好了
        TimeSlot timeSlot = new TimeSlot(start, length);
        timeSlot.setThisSlotPrice((lastHw.add(bigDecimal)).multiply(new BigDecimal(length)).setScale(0, BigDecimal.ROUND_DOWN).toString());
        timeSlots.add(timeSlot);

        return timeSlots;
    }

    /* 获取今日切割后的能做工作的时间与价格 */
    private List<LocalTimeAndPricePOJO> enableTimeToday(List<TimeSlotPOJO> times){
        List<LocalTimeAndPricePOJO> enableTimeToday = new ArrayList<>();
        times.forEach(time -> {
            LocalTime start = time.getTimeSlotStart();
            Float length = time.getTimeSlotLength();
            Float total = length / 0.5f;
            for (Float i = 0f; i < total; i++) {
                enableTimeToday.add(new LocalTimeAndPricePOJO(start, time.getHourlyWage(), time.getCode()));
                start = start.plusMinutes(30);
            }
        });
        return enableTimeToday;
    }

    @Override
    public Boolean judgeToday(List<LocalTimeAndPricePOJO> table, List<LocalTime> item) {
        AtomicReference<Boolean> todayIsOk = new AtomicReference<>(true);
        item.forEach(a -> {
            AtomicReference<Boolean> bool1 = new AtomicReference<>(false); //时段是否包含
            table.forEach(b -> {
                if (a.equals(b.getTime())) {
                    bool1.set(true);
                }
            });
            if (!bool1.get()) todayIsOk.set(false); //如果这个时段不行，那么今天就不行
        });
        return todayIsOk.get();
    }

    @Override
    public List<TimeSlot> withPriceOfSlot(List<LocalTime> item, List<LocalTimeAndPricePOJO> enableTimeToday) {
        /* 价格格式处理 */
        Map<LocalTime, LocalTimeAndPricePOJO> ltpMap = new HashMap<>();
        enableTimeToday.forEach(ett -> {
            ltpMap.put(ett.getTime(), ett);
        });

        /* 带价格的时间段生成 */
        List<TimeSlot> timeSlots = new ArrayList<>();
        LocalTime start = LocalTime.MIN; //当前段的开始时间
        Float length = 0f;//当前段累计长度
        BigDecimal lastHw = new BigDecimal(-1); //上一个的时薪记录
        LocalTime lastTime = LocalTime.MIN; //上一个遍历到的时间记录

        for (LocalTime time : item) {
            //生成新段的条件 价格跳段 或 时间段跳段
            LocalTimeAndPricePOJO ltp = ltpMap.get(time); //当前半小时，价格
            BigDecimal a = ltp.getHourlyWage(); //当前半小时的时薪
            LocalTime b = time;  //当前半小时

            //生成新段判断  价格跳段 或 时间段跳段
            if (!a.equals(lastHw) || !lastTime.plusMinutes(30).equals(b)){
                //结束上一段,将上一段放进结果中
                if (length!=0) {
                    TimeSlot timeSlot = new TimeSlot(start, length);
                    timeSlot.setThisSlotPrice(lastHw.multiply(new BigDecimal(length)).setScale(0, BigDecimal.ROUND_DOWN).toString());
                    timeSlots.add(timeSlot);
                }
                //开始新段
                start = b; length = 0f;
            }

            //老段延长
            length += 0.5f;

            //最后必做的事~更新这两个值
            lastHw = a; lastTime = b;
        }

        //最后还存留一段时间段长度不小于0.5h的时间段，只需要上传就好了
        TimeSlot timeSlot = new TimeSlot(start, length);
        timeSlot.setThisSlotPrice(lastHw.multiply(new BigDecimal(length)).setScale(0, BigDecimal.ROUND_DOWN).toString());
        timeSlots.add(timeSlot);

        return timeSlots;
    }

    /*時間段合理性判斷   假設都不為空*/
    public List<String> rationalityJudgmentA(SetEmployeesCalendarDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        SortListUtil<TimeSlotDTO> sortList = new SortListUtil<TimeSlotDTO>();
        List<TimeSlotDTO> timeSlotDTOS = dto.getTimeSlotList();
        sortList.Sort(timeSlotDTOS, "getTimeSlotStart", null);
        for (int i = 0; i < timeSlotDTOS.size()-1; i++) {
            PeriodOfTime period1 = new PeriodOfTime(timeSlotDTOS.get(i).getTimeSlotStart(), timeSlotDTOS.get(i).getTimeSlotLength());
            PeriodOfTime period2 = new PeriodOfTime(timeSlotDTOS.get(i+1).getTimeSlotStart(), timeSlotDTOS.get(i+1).getTimeSlotLength());
            if (CommonUtils.doRechecking(period1, period2)){
                //重複的處理方式
                StringBuilder res = new StringBuilder();
                res.append("通用模板存在時間段重複：");
                res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                res.append("與");
                res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                resCollections.add(res.toString());
            }
        }
        return resCollections;
    }
    /*時間段合理性判斷：周   假設都不為空*/
    public List<String> rationalityJudgmentB(SetEmployeesCalendarWeekDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        Map<Integer, List<PeriodOfTime>> map = new HashMap<>();
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", dto.getEmployeesId());
        qw.eq("stander", true);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        employeesCalendarList.forEach(calendar -> {
            List<Integer> weekList = new ArrayList<>();
            String weekStr = calendar.getWeek();
            for (int i = 0; i < weekStr.length(); i++) {
                weekList.add(Integer.valueOf(weekStr.charAt(i)-48));
            }
            PeriodOfTime period = new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
            weekList.forEach(week -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(period);
                map.put(week, exist);
            });
        });
        /*已准备好现有数据*/

        List<Integer> weekList = dto.getWeek();
        List<TimeSlotDTO> timeSlotDTOS = dto.getTimeSlotList();
        weekList.forEach(week -> {
            timeSlotDTOS.forEach(timeSlot -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(new PeriodOfTime(timeSlot.getTimeSlotStart(), timeSlot.getTimeSlotLength()));
                map.put(week, exist);
            });
            /*下面对一周的每一天进行筛查*/
            List<PeriodOfTime> existDay = map.getOrDefault(week, new ArrayList<>());
            SortListUtil<PeriodOfTime> sortList = new SortListUtil<PeriodOfTime>();
            sortList.Sort(existDay, "getTimeSlotStart", null);
            for (int i = 0; i < existDay.size()-1; i++) {
                PeriodOfTime period1 = existDay.get(i);
                PeriodOfTime period2 = existDay.get(i+1);
                if (CommonUtils.doRechecking(period1, period2)){
                    //重複的處理方式
                    StringBuilder res = new StringBuilder();
                    res.append("周模板存在時間段重複： week ").append(week).append("  ");
                    res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                    res.append("與");
                    res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                    resCollections.add(res.toString());
                }
            }
        });
        return resCollections;
    }
    /*時間段合理性判斷：日期   假設都不為空*/
    public List<String> rationalityJudgmentC(SetEmployeesCalendarDateDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", dto.getEmployeesId());
        qw.eq("stander", false);
        qw.eq("date", dto.getDate());
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        List<PeriodOfTime> periodOfTimeList1 = employeesCalendarList.stream().map(calendar -> {
            return new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
        }).collect(Collectors.toList());
        List<TimeSlotDTO> timeSlotDTOList = dto.getTimeSlotList();
        List<PeriodOfTime> periodOfTimeList2 = timeSlotDTOList.stream().map(calendar -> {
            return new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
        }).collect(Collectors.toList());
        periodOfTimeList1.addAll(periodOfTimeList2);
        /* periodOfTimeList1是当天的全部时间段 */
        SortListUtil<PeriodOfTime> sortList = new SortListUtil<PeriodOfTime>();
        sortList.Sort(periodOfTimeList1, "getTimeSlotStart", null);
        for (int i = 0; i < periodOfTimeList1.size()-1; i++) {
            PeriodOfTime period1 = periodOfTimeList1.get(i);
            PeriodOfTime period2 = periodOfTimeList1.get(i+1);
            if (CommonUtils.doRechecking(period1, period2)){
                //重複的處理方式
                StringBuilder res = new StringBuilder();
                res.append("日期模板存在時間段重複： date ").append(dto.getDate()).append("  ");
                res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                res.append("與");
                res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                resCollections.add(res.toString());
            }
        }
        return resCollections;
    }
    /*時間段合理性判斷：周   假設都不為空*/
    public List<String> rationalityJudgmentD(SetEmployeesCalendar2DTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        Map<Integer, List<PeriodOfTime>> map = new HashMap<>();
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", dto.getEmployeesId());
        qw.eq("stander", true);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        employeesCalendarList.forEach(calendar -> {
            List<Integer> weekList = new ArrayList<>();
            String weekStr = calendar.getWeek();
            for (int i = 0; i < weekStr.length(); i++) {
                weekList.add(Integer.valueOf(weekStr.charAt(i)-48));
            }
            PeriodOfTime period = new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
            weekList.forEach(week -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(period);
                map.put(week, exist);
            });
        });
        /*已准备好现有数据*/

        List<Integer> weekList = dto.getWeek();
        List<TimeSlotPriceDTO> timeSlotDTOS = dto.getTimeSlotPriceDTOList();
        weekList.forEach(week -> {
            timeSlotDTOS.forEach(timeSlot -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(new PeriodOfTime(timeSlot.getTimeSlotStart(), timeSlot.getTimeSlotLength()));
                map.put(week, exist);
            });
            /*下面对一周的每一天进行筛查*/
            List<PeriodOfTime> existDay = map.getOrDefault(week, new ArrayList<>());
            SortListUtil<PeriodOfTime> sortList = new SortListUtil<PeriodOfTime>();
            sortList.Sort(existDay, "getTimeSlotStart", null);
            for (int i = 0; i < existDay.size()-1; i++) {
                PeriodOfTime period1 = existDay.get(i);
                PeriodOfTime period2 = existDay.get(i+1);
                if (CommonUtils.doRechecking(period1, period2)){
                    //重複的處理方式
                    StringBuilder res = new StringBuilder();
                    res.append("周模板存在時間段重複： week ").append(week).append("  ");
                    res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                    res.append("與");
                    res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                    resCollections.add(res.toString());
                }
            }
        });
        return resCollections;
    }
    /*時間段合理性判斷：周   假設都不為空*/
    public List<String> rationalityJudgmentE(UpdateEmployeesCalendarDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        Map<Integer, List<PeriodOfTime>> map = new HashMap<>();
        EmployeesCalendar ec = this.getById(dto.getId());
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", ec.getEmployeesId());
        qw.eq("stander", true);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        employeesCalendarList.forEach(calendar -> {
            if (calendar.getId().equals(ec.getId())) return;   //把当前dto要暂时删掉
            List<Integer> weekList = new ArrayList<>();
            String weekStr = calendar.getWeek();
            for (int i = 0; i < weekStr.length(); i++) {
                weekList.add(Integer.valueOf(weekStr.charAt(i)-48));
            }
            PeriodOfTime period = new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
            weekList.forEach(week -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(period);
                map.put(week, exist);
            });
        });
        /*已准备好现有数据*/

        List<Integer> weekList = dto.getWeeks();
        weekList.forEach(week -> {
            List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
            exist.add(new PeriodOfTime(dto.getTimeSlotStart(), dto.getTimeSlotLength()));
            map.put(week, exist);
            /*下面对一周的每一天进行筛查*/
            List<PeriodOfTime> existDay = map.getOrDefault(week, new ArrayList<>());
            SortListUtil<PeriodOfTime> sortList = new SortListUtil<PeriodOfTime>();
            sortList.Sort(existDay, "getTimeSlotStart", null);
            for (int i = 0; i < existDay.size()-1; i++) {
                PeriodOfTime period1 = existDay.get(i);
                PeriodOfTime period2 = existDay.get(i+1);
                if (CommonUtils.doRechecking(period1, period2)){
                    //重複的處理方式
                    StringBuilder res = new StringBuilder();
                    res.append("周模板存在時間段重複： week ").append(week).append("  ");
                    res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                    res.append("與");
                    res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                    resCollections.add(res.toString());
                }
            }
        });
        return resCollections;
    }

    private String authenticationProcessing(Integer employeesId){
        String roleType = TokenUtils.getRoleType();
        if(roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN)){
            return CommonConstants.AUTHENTICATION_SUCCESSFUL;//直接鉴权放行
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            //需要检查公司账户下有没有这个保洁员id
            return companyDetailsService.thereIsACleaner(employeesId) ? CommonConstants.AUTHENTICATION_SUCCESSFUL : "保潔員不存在";
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_CUSTOMER)){
            return "客戶怎麼能調這個藉口";
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            /* 判断该保洁员受不受到我管辖 */
            return managerDetailsService.thereIsACleaner(employeesId) ? CommonConstants.AUTHENTICATION_SUCCESSFUL : "該保潔員不被您管轄或者保潔員不存在";
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            /* 判斷保潔員是不是我 */
            return employeesDetailsService.isMe(employeesId) ? CommonConstants.AUTHENTICATION_SUCCESSFUL : "保潔員只能設置自己的時間表";
        }else {
            return CommonConstants.AUTHENTICATION_FAILED;
        }
    }

    public List<FreeDateTimeDTO> getCalendarByDateSlot2(GetCalendarByDateSlotDTO dto) {
        List<FreeDateTimeDTO> freeDateTimeDTOS = new ArrayList<>();

        /* 先得到三大map */
        SortListUtil<TimeSlotPOJO> sort = new SortListUtil<>();
        Map<LocalDate, List<TimeSlotPOJO>> map1 = new HashMap<>();
        Map<Integer, List<TimeSlotPOJO>> map2 = new HashMap<>();
        Map<String, List<TimeSlotPOJO>> map3 = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", dto.getId());
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);

        employeesCalendarList.forEach(ec -> {
            TimeSlotPOJO pojo = new TimeSlotPOJO();
            pojo.setTimeSlotStart(ec.getTimeSlotStart());
            pojo.setTimeSlotLength(ec.getTimeSlotLength());
            pojo.setType(ec.getType());
            pojo.setPercentage(ec.getPercentage());
            pojo.setCode(ec.getCode());
            pojo.setHourlyWage(ec.getHourlyWage());
            if (CommonUtils.isEmpty(ec.getStander())){
                List<TimeSlotPOJO> pojoList = map3.getOrDefault("", new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map3.put("", pojoList);
            }else if (ec.getStander() == false){ //日期
                List<TimeSlotPOJO> pojoList = map1.getOrDefault(ec.getDate(), new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map1.put(ec.getDate(), pojoList);
            }else if (ec.getStander() == true){ //周
                String weekString = ec.getWeek();
                for (int i = 0; i < weekString.length(); i++) {
                    Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                    List<TimeSlotPOJO> pojoList = map2.getOrDefault(weekInteger, new ArrayList<>());
                    pojoList.add(pojo);
                    sort.Sort(pojoList, "getTimeSlotStart", null);
                    map2.put(weekInteger, pojoList);
                }
            }
        });
        /* 先得到三大map */


        LocalDate start = dto.getDateSlot().getStart();
        LocalDate end = dto.getDateSlot().getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            FreeDateTimeDTO freeDateTimeDTO = new FreeDateTimeDTO();
            List<TimeSlotPOJO> todaySlot = new ArrayList<>();
            if (map1.containsKey(date)){
                //日期模板生效
                todaySlot = map1.get(date);
            }else if (!map2.isEmpty()){
                //周模板生效
                todaySlot = map2.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>());
            }else if (!map3.isEmpty()){
                //通用模板生效
                todaySlot = map3.get("");
            }
            if (todaySlot.size() != 0){
                freeDateTimeDTO.setDate(date);
                freeDateTimeDTO.setTimes(todaySlot);
                freeDateTimeDTO.setHasTime(true);
            }else {
                freeDateTimeDTO.setDate(date);
                freeDateTimeDTO.setTimes(new ArrayList<>());
                freeDateTimeDTO.setHasTime(false);
            }
            freeDateTimeDTO.setWeek(date.getDayOfWeek().getValue());
            freeDateTimeDTOS.add(freeDateTimeDTO);
        }
        return freeDateTimeDTOS;
    }

    private BigDecimal priceAfterDiscount(List<WorkDetailsPOJO> workDetails){

        return new BigDecimal(0);
    }

    private BigDecimal todayPrice(List<LocalTimeAndPricePOJO> table, List<LocalTime> item){
        BigDecimal todayPrice = new BigDecimal(0);
        for (LocalTime x : item) {
            for (LocalTimeAndPricePOJO y : table) {
                if (y.getTime().equals(x)){
                    BigDecimal hourlyWage = y.getHourlyWage();//已转换成TWD的时薪
                    BigDecimal semihWage = hourlyWage.divide(new BigDecimal(2)).setScale(0, BigDecimal.ROUND_DOWN);
                    todayPrice = todayPrice.add(semihWage);
                    break;
                }
            }
        }
        return todayPrice;
    }

    public BigDecimal totalPrice(List<WorkDetailsPOJO> workDetails){
        BigDecimal totalPrice = new BigDecimal(0);
        for (WorkDetailsPOJO x : workDetails) {
            totalPrice = totalPrice.add(x.getTodayPrice());
        }

        return totalPrice;
    }

    @Override
    public Integer days(List<WorkDetailsPOJO> workDetails) {
        Long days = workDetails.stream().filter(WorkDetailsPOJO::getCanBeOnDuty).count();
        return new Integer(Math.toIntExact(days));
    }

    @Override
    public Float hOfDay(MakeAnAppointmentDTO dto) {
        AtomicReference<Float> h  = new AtomicReference<>(new Float(0));
        dto.getTimeSlots().forEach(timeSlot -> {
            h.set(h.get() + timeSlot.getTimeSlotLength());
        });
        return h.get();
    }

    public Float hOfDay(MakeAnAppointmentByDateDTO dto) {
        AtomicReference<Float> h  = new AtomicReference<>(new Float(0));
        dto.getTimeSlots().forEach(timeSlot -> {
            h.set(h.get() + timeSlot.getTimeSlotLength());
        });
        return h.get();
    }

    @Override
    public Map<LocalDate, TodayDetailsPOJO> getCalendar(GetCalendarByDateSlotDTO dto) {
        /* 先得到三大map */
        SortListUtil<TimeSlotPOJO> sort = new SortListUtil<>();
        Map<LocalDate, List<TimeSlotPOJO>> map1 = new HashMap<>();
        Map<Integer, List<TimeSlotPOJO>> map2 = new HashMap<>();
        Map<String, List<TimeSlotPOJO>> map3 = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", dto.getId());
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        if (CommonUtils.isEmpty(employeesCalendarList)){
            return null;
        }

        employeesCalendarList.forEach(ec -> {
            TimeSlotPOJO pojo = new TimeSlotPOJO();
            pojo.setTimeSlotStart(ec.getTimeSlotStart());
            pojo.setTimeSlotLength(ec.getTimeSlotLength());
            pojo.setType(ec.getType());
            pojo.setPercentage(ec.getPercentage());
            pojo.setHourlyWage(ec.getHourlyWage());
            pojo.setCode(ec.getCode());
            if (CommonUtils.isEmpty(ec.getStander())){
                List<TimeSlotPOJO> pojoList = map3.getOrDefault("", new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map3.put("", pojoList);
            }else if (ec.getStander() == false){ //日期
                List<TimeSlotPOJO> pojoList = map1.getOrDefault(ec.getDate(), new ArrayList<>());
                pojoList.add(pojo);
                sort.Sort(pojoList, "getTimeSlotStart", null);
                map1.put(ec.getDate(), pojoList);
            }else if (ec.getStander() == true){ //周
                String weekString = ec.getWeek();
                for (int i = 0; i < weekString.length(); i++) {
                    Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                    List<TimeSlotPOJO> pojoList = map2.getOrDefault(weekInteger, new ArrayList<>());
                    pojoList.add(pojo);
                    sort.Sort(pojoList, "getTimeSlotStart", null);
                    map2.put(weekInteger, pojoList);
                }
            }
        });
        /* 先得到三大map */

        /* 從而演化出時間表 */
        Map<LocalDate, TodayDetailsPOJO> calendarMap = new HashMap<>();
        LocalDate start = dto.getDateSlot().getStart();
        LocalDate end = dto.getDateSlot().getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            TodayDetailsPOJO tdp = new TodayDetailsPOJO();
            List<TimeSlotPOJO> todaySlot = new ArrayList<>();
            if (map1.containsKey(date)){
                //日期模板生效
                todaySlot = map1.get(date);
            }else if (!map2.isEmpty()){
                //周模板生效
                todaySlot = map2.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>());
            }else if (!map3.isEmpty()){
                //通用模板生效
                todaySlot = map3.get("");
            }
            if (todaySlot.size() != 0){
                tdp.setTimes(todaySlot);
                tdp.setHasTime(true);
            }else {
                tdp.setTimes(new ArrayList<>());
                tdp.setHasTime(false);
            }
            tdp.setWeek(date.getDayOfWeek().getValue());
            calendarMap.put(date, tdp);
        }
        return calendarMap;
    }

    @Override
    public Map<LocalDate, TodayDetailsPOJO> getCalendarFreeTime(GetCalendarByDateSlotDTO dto) {
        return this.getCalendar(dto);
    }

    public List<WorkDetailsPOJO> makeAnAppointmentHandles(MakeAnAppointmentDTO dto, Boolean need,Integer jobId) {

        List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();

        List<FreeDateTimeDTO> freeTime = null;

        EmployeesDetails byId = employeesDetailsService.getById(dto.getEmployeesId());
        /* 获取这段日期内的空闲时间 */
                //判断员工是否会该项工作
        if(CommonUtils.isEmpty(byId.getPresetJobIds())){
            freeTime = this.getFreeTimeByDateSlot4(new GetCalendarByDateSlotDTO(new DateSlot(dto.getStart(), dto.getEnd()), dto.getEmployeesId()));
        }else {
            if(byId.getPresetJobIds().contains(jobId.toString())){
                freeTime = this.getFreeTimeByDateSlot5(new GetCalendarByDateSlotDTO(new DateSlot(dto.getStart(), dto.getEnd()), dto.getEmployeesId()),jobId.toString());
            }else{
                freeTime = this.getFreeTimeByDateSlot4(new GetCalendarByDateSlotDTO(new DateSlot(dto.getStart(), dto.getEnd()), dto.getEmployeesId()));
            }
        }

        freeTime.forEach(x -> {
            /* 今日数据准备 */
            LocalDate today = x.getDate(); //今日日期
            Integer todayWeek = today.getDayOfWeek().getValue();
            if (!dto.getWeeks().contains(todayWeek)) return;     //如果周数没有这天，那么就跳过吧
            List<LocalTimeAndPricePOJO> enableTimeToday = this.enableTimeToday(x.getTimes());//切割的时间表与价格
            List<LocalTime> item = sysIndexService.periodSplittingB(dto.getTimeSlots());//切割的需求时间段
            Boolean todayIsOk = this.judgeToday(enableTimeToday, item);         //判断今天行不行
            List<TimeSlot> timeSlots = new ArrayList<>();
            if (need) {
                if(todayIsOk) timeSlots = this.withPriceOfSlot(item, enableTimeToday); //给时段加价格
                else timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }else {
                timeSlots = dto.getTimeSlots();  //不用给时段加价格了
            }

            Boolean canBeOnDuty = true;
            BigDecimal todayPrice = new BigDecimal(0);
            if (canBeOnDuty) todayPrice = this.todayPrice(enableTimeToday, item);
            /* 今日数据返回 */
            WorkDetailsPOJO wdp = new WorkDetailsPOJO(today, todayWeek, timeSlots, canBeOnDuty, todayPrice);
            workDetailsPOJOS.add(wdp);
        });
        return workDetailsPOJOS;
    }

    @Override
    public R getAllInCompany() {
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        QueryWrapper qw = new QueryWrapper();
        qw.eq("company_id", companyId);
        qw.select("id");
        List<Integer> employeesIds = employeesDetailsService.listObjs(qw); //公司所有保洁员
        QueryWrapper qw2 = new QueryWrapper();
        qw2.in("employees_id", employeesIds);
        List<EmployeesCalendar> calendars = employeesCalendarService.list(qw2);

        return R.ok(calendars, "獲取成功");
    }

    @Override
    public R setCalendarAll(SetCalendarAllDTO dto) {
        /* 獲取公司所有保潔員 */
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        QueryWrapper qw = new QueryWrapper();
        qw.eq("company_id", companyId);
        qw.select("id");
        List<Integer> employeesIds = employeesDetailsService.listObjs(qw); //公司所有保洁员

        /* 为每个保洁员添加新的时间表 */
        StringBuilder week = new StringBuilder();
        dto.getWeek().forEach(wk->{
            week.append(wk);
        });

        Map<Integer, List> res = new HashMap<>();
        employeesIds.forEach(employeesId -> {
            SetEmployeesCalendar2DTO setEmployeesCalendar2DTO = new SetEmployeesCalendar2DTO(employeesId, dto.getWeek(), dto.getTimeSlotPriceDTOList());
            List<String> info = this.rationalityJudgmentD(setEmployeesCalendar2DTO); //不合理记录收集
            if (info.size() == 0){
                //这是合理的,就进行添加时间表
                dto.getTimeSlotPriceDTOList().forEach(timeSlot -> {
                    EmployeesCalendar employeesCalendar =
                            new EmployeesCalendar(
                                    employeesId,
                                    true,
                                    null,
                                    week.toString(),
                                    timeSlot.getTimeSlotStart(),
                                    timeSlot.getTimeSlotLength()
                            );
                    employeesCalendar.setHourlyWage(new BigDecimal(timeSlot.getPrice()));
                    employeesCalendar.setCode(timeSlot.getCode());
                    baseMapper.insert(employeesCalendar);
                });
                info.add("添加成功");
            }
            res.put(employeesId, info);
        });

        return R.ok(res, "設置成功，設置結果已返回");
    }

    @Override
    public R getSchedulingByEmployeesId(Integer employeesId) {
        /* 保洁员存在性判断 */
        Boolean existing = employeesDetailsService.judgmentOfExistence(employeesId);
        if (!existing) return R.failed(null, "保潔員不存在");

        /* 數據從數據庫獲取 */
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        List<EmployeesCalendar> re = employeesCalendarService.list(qw);

        /* 時間表存在性判斷 */
        if (re.isEmpty()) return R.failed(null, "該保潔員未設置時間表");
        List<CalendarPOJO> cps = re.stream().map(x -> {
            return new CalendarPOJO(x);
        }).collect(Collectors.toList());

        /* 返回信息 */
        return R.ok(cps, "獲取成功");
    }

    @Override
    public R getFreeTimeByMonth2(GetFreeTimePriceByMonthDTO dto) {

        Integer empId = dto.getEmployeesId();
        EmployeesDetails byId = employeesDetailsService.getById(empId);
        List<String> skills = Arrays.asList(byId.getPresetJobIds().split(" "));
        List<String> price = Arrays.asList(byId.getJobPrice().split(" "));
        int i1 = skills.indexOf(dto.getJobId().toString());
        BigDecimal workPrice = new BigDecimal(price.get(i1));

        if (dto.getMonth()>12 || dto.getMonth()<1) return R.failed(null, "月份錯誤");
        if (dto.getYear() < LocalDate.now().getYear()) return R.failed(null, "年份不能选择以前");

        LocalDate thisMonthFirstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
        //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
        //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);
        List<FreeDateTimePriceDTO> freeTime =  getFreeTimeByDateSlot3(new GetCalendarByDateSlotDTO(new DateSlot(startDate, endDate), dto.getEmployeesId()),dto.getJobId());
        List<CalendarPriceDTO> list = freeTime.stream().map(x -> {
            CalendarPriceDTO calendarInfoDTO = new CalendarPriceDTO(x);
            calendarInfoDTO.setIsThisMonth(x.getDate().getMonth().getValue() == dto.getMonth());
            calendarInfoDTO.setIsAfter(x.getDate().isAfter(LocalDate.now())||x.getDate().equals(LocalDate.now()));
            return calendarInfoDTO;
        }).collect(Collectors.toList());

        return R.ok(list,"獲取成功");
    }

    @Override
    public R getSchedulingById(Integer companyId) {
        List<CompanyCalendar> companyCalendars = companyCalendarService.getCalendar(companyId);
        return R.ok(companyCalendars);
    }

    @Override
    public R setSchedulingByIds(setSchedulingVO vo) {
        List<CompanyCalendar> companyCalendarList = vo.getCalendarIds().stream().map(x -> companyCalendarService.getById(x)).collect(Collectors.toList());
        List<EmployeesCalendar> calendars = companyCalendarList.stream().map(cc -> {
            EmployeesCalendar ec = new EmployeesCalendar(
                    vo.getEmpId(),
                    cc.getStander(),
                    cc.getDate(),
                    cc.getWeek(),
                    cc.getTimeSlotStart(),
                    cc.getTimeSlotLength()
            );
            ec.setHourlyWage(cc.getHourlyWage());
            ec.setCode(cc.getCode());
            ec.setType(cc.getType());
            ec.setPercentage(cc.getPercentage());
            return ec;
        }).collect(Collectors.toList());
        for (int i = 0; i < calendars.size(); i++) {
            QueryWrapper<EmployeesCalendar> qw = new QueryWrapper<>();
            qw.eq("employees_id",calendars.get(i).getEmployeesId());
            qw.eq("week",calendars.get(i).getWeek());
            qw.eq("time_slot_start",calendars.get(i).getTimeSlotStart());
            qw.eq("time_slot_length",calendars.get(i).getTimeSlotLength());
            if(this.count(qw)>0){
                return R.failed("時間衝突，請重新選擇");
            }
            employeesCalendarService.save(calendars.get(i));
        }
        return R.ok("添加成功");
    }

    @Override
    public List<FreeDateTimePriceDTO> getFreeTime(List<FreeDateTimePriceDTO> calendarByDateSlot, GetCalendarByDateSlotDTO dto) {
        //获取所有订单
        List<OrderDetailsPOJO> orderByEmpId = orderDetailsService.getOrderByEmpId(dto.getId());

        List<FreeDateTimePriceDTO> hasWork = new ArrayList<>();
        LocalDate start = dto.getDateSlot().getStart();
        LocalDate end = dto.getDateSlot().getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            LocalDate finalDate = date;
            FreeDateTimePriceDTO freeDateTimePriceDTO = new FreeDateTimePriceDTO();
            freeDateTimePriceDTO.setDate(finalDate);
            freeDateTimePriceDTO.setWeek(date.getDayOfWeek().getValue());
            List<TimeSlotPricePOJO> timeSlotPricePOJOS = new ArrayList<>();
            orderByEmpId.forEach(x ->{
                List<WorkDetailsPOJO> workDetails = x.getWorkDetails();
                workDetails.forEach(y ->{
                    LocalDate date1 = y.getDate();
                    if(date1.equals(finalDate)){
                        List<TimeSlot> timeSlots = y.getTimeSlots();
                        timeSlots.forEach(z ->{
                            TimeSlotPricePOJO timeSlotPricePOJO = new TimeSlotPricePOJO();
                            timeSlotPricePOJO.setTimeSlotStart(z.getTimeSlotStart());
                            timeSlotPricePOJO.setTimeSlotLength(z.getTimeSlotLength());
                            timeSlotPricePOJOS.add(timeSlotPricePOJO);
                        });
                    }
                });
            });
            freeDateTimePriceDTO.setTimes(timeSlotPricePOJOS);
            hasWork.add(freeDateTimePriceDTO);
        }

        for (int i = 0; i < calendarByDateSlot.size(); i++) {

            FreeDateTimePriceDTO freeDateTimePriceDTO = calendarByDateSlot.get(i);
            List<TimeSlotPricePOJO> times = freeDateTimePriceDTO.getTimes();

            FreeDateTimePriceDTO freeDateTimePriceDTO1 = hasWork.get(i);
            List<TimeSlotPricePOJO> times1 = freeDateTimePriceDTO1.getTimes();


            times.forEach(x ->{
                ArrayList<TimeSlotPricePOJO> timeSlotPricePOJOS = new ArrayList<>();
                LocalTime timeSlotStart = x.getTimeSlotStart();
                LocalTime timeSlotEnd = timeSlotStart.plusMinutes((long) ((x.getTimeSlotLength()/0.5)*30));
                List<String> intervalTimeList = TimeUtils.getIntervalTimeList(timeSlotStart.toString(), timeSlotEnd.toString(), 30);
                for (int i1 = 0; i1 < intervalTimeList.size()-1; i1++) {
                    TimeSlotPricePOJO timeSlotPricePOJO = new TimeSlotPricePOJO();
                    timeSlotPricePOJO.setTimeSlotStart(LocalTime.parse(intervalTimeList.get(i1), DateTimeFormatter.ofPattern("HH:mm")));
                    timeSlotPricePOJO.setTimeSlotLength((float) 0.5);
                    timeSlotPricePOJO.setTotalPrice(x.getTotalPrice());
                    timeSlotPricePOJO.setCode(x.getCode());
                    timeSlotPricePOJO.setHourlyWage(x.getHourlyWage());
                    timeSlotPricePOJOS.add(timeSlotPricePOJO);
                }

                ArrayList<TimeSlotPricePOJO> timeSlotPricePOJOS2 = new ArrayList<>();
                times1.forEach(y ->{
                    LocalTime timeSlotStart2 = y.getTimeSlotStart();
                    LocalTime timeSlotEnd2 = timeSlotStart2.plusMinutes((long) ((y.getTimeSlotLength()/0.5)*30));
                    List<String> intervalTimeList2 = TimeUtils.getIntervalTimeList(timeSlotStart2.toString(), timeSlotEnd2.toString(), 30);
                    for (int i1 = 0; i1 < intervalTimeList2.size()-1; i1++) {
                        TimeSlotPricePOJO timeSlotPricePOJO = new TimeSlotPricePOJO();
                        timeSlotPricePOJO.setTimeSlotStart(LocalTime.parse(intervalTimeList2.get(i1), DateTimeFormatter.ofPattern("HH:mm")));
                        timeSlotPricePOJO.setTimeSlotLength((float) 0.5);
                        timeSlotPricePOJO.setTotalPrice(x.getTotalPrice());
                        timeSlotPricePOJO.setCode(x.getCode());
                        timeSlotPricePOJO.setHourlyWage(x.getHourlyWage());
                        timeSlotPricePOJOS2.add(timeSlotPricePOJO);
                    }
                });
                timeSlotPricePOJOS.removeAll(timeSlotPricePOJOS2);
                freeDateTimePriceDTO.setTimes(timeSlotPricePOJOS);
            });
        }

        calendarByDateSlot.forEach(x ->{
            List<TimeSlotPricePOJO> item = x.getTimes();

            /* 带价格的时间段生成 */
            List<TimeSlotPricePOJO> timeSlots = new ArrayList<>();
            LocalTime start2 = LocalTime.MIN; //当前段的开始时间
            Float length = 0f;//当前段累计长度
            LocalTime lastTime = LocalTime.MIN; //上一个遍历到的时间记录

            for (TimeSlotPricePOJO time : item) {
                //生成新段的条件 价格跳段 或 时间段跳段
                LocalTime b = time.getTimeSlotStart();  //当前半小时

                //生成新段判断  价格跳段 或 时间段跳段
                if (!lastTime.plusMinutes(30).equals(b)){
                    //结束上一段,将上一段放进结果中
                    if (length!=0) {
                        TimeSlotPricePOJO timeSlot = new TimeSlotPricePOJO();
                        timeSlot.setTimeSlotStart(start2);
                        timeSlot.setTimeSlotLength(length);
                        timeSlot.setTotalPrice(time.getTotalPrice());
                        timeSlot.setCode(time.getCode());
                        timeSlot.setHourlyWage(time.getHourlyWage());
                        timeSlots.add(timeSlot);
                    }
                    //开始新段
                    start2 = b; length = 0f;
                }

                //老段延长
                length += 0.5f;

                //最后必做的事~更新这两个值
                lastTime = b;
            }

            //最后还存留一段时间段长度不小于0.5h的时间段，只需要上传就好了
           if(CollectionUtils.isNotEmpty(x.getTimes())){
               TimeSlotPricePOJO timeSlot = new TimeSlotPricePOJO();
               timeSlot.setTimeSlotStart(start2);
               timeSlot.setTimeSlotLength(length);
               BigDecimal totalPrice;
               if(CollectionUtils.isNotEmpty(x.getTimes())){
                   totalPrice = x.getTimes().get(x.getTimes().size() - 1).getTotalPrice();
               }else{
                   totalPrice = null;
               }
               timeSlot.setTotalPrice(totalPrice);

               String code;
               if(CollectionUtils.isNotEmpty(x.getTimes())){
                   code = x.getTimes().get(x.getTimes().size() - 1).getCode();
               }else{
                   code = null;
               }
               timeSlot.setCode(code);

               BigDecimal hourlyWage;
               if(CollectionUtils.isNotEmpty(x.getTimes())){
                   hourlyWage = x.getTimes().get(x.getTimes().size() - 1).getHourlyWage();
               }else{
                   hourlyWage = null;
               }
               timeSlot.setHourlyWage(hourlyWage);
               timeSlots.add(timeSlot);
           }
            x.setTimes(timeSlots);
           if(CollectionUtils.isEmpty(x.getTimes())){
               x.setHasTime(false);
           }
        });

        return calendarByDateSlot;
    }

    @Override
    public R confirmOrder(MakeAnAppointmentDTO dto) {
        if(CollectionUtils.isEmpty(dto.getJobIds())){
            return R.failed("請選擇工作內容!");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();

        /* 订单来源 */
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_CALENDAR);

        /* 订单编号 */
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        /* 流水号 */
        String serialNumber = serialNumberService.generateSerialNumber(number);
        odp.setSerialNumber(serialNumber);

        /* 消费项目 */
        odp.setConsumptionItems("1");

        /* 订单甲方 保洁员 */
        Boolean exist = employeesDetailsService.judgmentOfExistence(dto.getEmployeesId());
        if (!exist) return R.failed(null, "保潔員不存在");
        EmployeesDetails ed = employeesDetailsService.getById(dto.getEmployeesId());
        odp.setEmployeesId(ed.getId());
        odp.setName1(ed.getName());
        odp.setPhPrefix1(ed.getPhonePrefix());
        odp.setPhone1(ed.getPhone());

        /* 甲方所属公司 */
        if(ed.getCompanyId()!=null){
            CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
            odp.setCompanyId(cod.getId().toString());
            odp.setInvoiceName(cod.getInvoiceName());
            odp.setInvoiceNumber(cod.getInvoiceNumber());
        }

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        CustomerAddress ca = customerAddressService.getById(dto.getAddressId());
        odp.setCustomerId(cd.getId());
        odp.setName2(ca.getName());
        odp.setPhPrefix2(ca.getPhonePrefix());
        odp.setPhone2(ca.getPhone());

        /* 订单工作内容 */
        String jobIds = CommonUtils.arrToString(dto.getJobIds().toArray(new Integer[0]));
        odp.setJobIds(jobIds);

        /* 订单工作筆記 */
        if(CollectionUtils.isEmpty(dto.getNotes())){
            odp.setNoteIds(null);
        }else {
            String noteIds = CommonUtils.arrToString(dto.getNotes().toArray(new Integer[0]));
            odp.setNoteIds(noteIds);
        }


        /* 地址 */
        odp.setAddress(ca.getAddress());
        odp.setLat(new Float(ca.getLat()));
        odp.setLng(new Float(ca.getLng()));


        /* 工作时间安排 */
        List<WorkDetailsPOJO> wds = this.makeAnAppointmentHandle(dto, true);
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = this.days(wds);
        odp.setDays(days);

        /* 每日工作时长计算 */
        Float h = this.hOfDay(dto);
        odp.setHOfDay(h);

        /* 原价格计算 */
        BigDecimal pdb = this.totalPrice(wds);
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

        /* 订单状态 */
        odp.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);//待支付状态

        /* 订单生成时间 */
        odp.setStartDateTime(now);
        odp.setUpdateDateTime(now);

        int hourly;
        /* 订单截止付款时间 保留时间 */
        if(ed.getCompanyId()!=null){
            hourly = orderDetailsService.orderRetentionTime(dto.getEmployeesId());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }else{
            QueryWrapper qw6 = new QueryWrapper();
            qw6.eq("config_key", ApplicationConfigConstants.orderRetentionTime);
            SysConfig config = configService.getOne(qw6);
            hourly = Integer.parseInt(config.getConfigValue());
            LocalDateTime payDeadline = now.plusHours(hourly);
            odp.setPayDeadline(payDeadline);
            odp.setH(hourly);
        }

        String key = "OrderToBePaid:employeesId"+dto.getEmployeesId()+":" + number;
        Map<String, Object> map = new HashMap<>();
        try {
            map = CommonUtils.objectToMap(odp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, hourly, TimeUnit.HOURS);
        serialService.generatePipeline(odp);

        ConfirmOrderPOJO confirmOrderPOJO = new ConfirmOrderPOJO(odp);
        List<Integer> noteIds = CommonUtils.stringToList(confirmOrderPOJO.getNoteIds());
        if(CollectionUtils.isNotEmpty(noteIds)){
            List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
            confirmOrderPOJO.setNotes(sysJobNotes);
        }
        return R.ok(confirmOrderPOJO,"预约成功");
    }

}
