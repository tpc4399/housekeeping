package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.QueryMapper;
import com.housekeeping.admin.pojo.*;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.ScoreCalculation;
import com.housekeeping.common.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @create 2021/5/9 11:37
 */
@Service("queryService")
public class QueryServiceImpl implements IQueryService {

    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesContractService employeesContractService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private QueryMapper queryMapper;
    @Resource
    private ISysConfigService sysConfigService;
    @Resource
    private IEmployeesPromotionService employeesPromotionService;

    @Override
    public R query(QueryDTO dto) throws InterruptedException {
        List<String> resFailed = paramsEmpty(dto);
        if (resFailed.size() != 0) return R.failed(resFailed, "存在空值");    //參數判空

        Map<String, String> weight = sysConfigService.getQueryWeight(dto.getPriorityType());  //權重
        List<Integer> searchPool = this.searchPool(dto.getCertified());                       //搜索池
        List<IndexResultPOJO> resultPOJOS = Collections.synchronizedList(new ArrayList<>());    //结果池
        List<EmployeesPOJO> employeesPOJOS = Collections.synchronizedList(new ArrayList<>());  //员工结果与分數
        List<CompanyPOJO> companyPOJOS = Collections.synchronizedList(new ArrayList<>());  //公司结果与分數

        Map<Integer, Float> companyScope = new Hashtable<>();         //公司分数

        //员工处理线程池
        ExecutorService exr1 = Executors.newCachedThreadPool();
        for (int i = 0; i < searchPool.size(); i++) {
            int finalI = i;
            exr1.submit(() -> {
                Integer employeesId = searchPool.get(finalI);
                EmployeesDetails ed = employeesDetailsService.getById(employeesId);

                GetCalendarByDateSlotDTO gc = new GetCalendarByDateSlotDTO();
                DateSlot ds = new DateSlot();
                ds.setStart(dto.getStart());
                ds.setEnd(dto.getEnd());
                gc.setDateSlot(ds);
                gc.setId(employeesId);
                Map<LocalDate, TodayDetailsPOJO> calendarFreeTime = employeesCalendarService.getCalendarFreeTime(gc);

                QueryWrapper qw = new QueryWrapper();
                qw.eq("employees_id", employeesId);
                List<EmployeesContract> ecs1 = employeesContractService.list(qw);
                List<EmployeesCalendar> ecs2 = employeesCalendarService.list(qw);
                List<SysJobContend> skillTags = (List<SysJobContend>) employeesCalendarService.getSkillTags(employeesId).getData();

                /** certified:員工認證準備 */
                CompanyDetails cd = companyDetailsService.getById(ed.getCompanyId());
                Boolean certified = cd.getIsValidate();

                Float variable1 = this.variable1(calendarFreeTime, dto); //钟点工空闲時間匹配率
                Float variable2 = this.variable2(ed.getPresetJobIds(), dto); //钟点工工作內容匹配率   如果不能匹配，那就是0
                Float variable4 = this.variable4(ecs1, dto); //包工工作内容匹配率   如果不能匹配，那就是0
                BigDecimal variable5 = this.variable5(ecs1, ecs2); //最低時薪
                String variable6 = this.variable6(ed, dto); //距離
                Float variable7 = this.variable7(ed); //評價星級
                Boolean variable8 = this.variable8(employeesId); //推广
                Float scope = new ScoreCalculation(variable1, variable2, variable4, variable5, variable6, variable7, variable8, weight, dto.getLowHourlyWage(), dto.getHighHourlyWage(), dto.getPriorityType()).scope();

                //构造对象
                EmployeesDetailsPOJO edp = new EmployeesDetailsPOJO();
                edp.setEmployeesId(employeesId);
                edp.setName(ed.getName());
                edp.setBirthDate(ed.getDateOfBirth());
                edp.setWorkYear(ed.getWorkYear());
                edp.setNumberOfOrder(ed.getNumberOfOrders());
                edp.setHeaderUrl(ed.getHeadUrl());
                edp.setStarRating(ed.getStarRating());
                edp.setAddressDTO(new AddressDetailsDTO(ed.getAddress1()+ed.getAddress2()+ed.getAddress3()+ed.getAddress4(), new Float(ed.getLng()), new Float(ed.getLat())));
                edp.setHourlyWage(variable5);
                edp.setCode("TWD");
                edp.setInstances(variable6);
                edp.setSkillTags(skillTags);
                edp.setCertified(certified);
                EmployeesPOJO employeesPOJO = new EmployeesPOJO();
                employeesPOJO.setScope(scope);
                employeesPOJO.setEmployeesDetailsPOJO(edp);
                employeesPOJOS.add(employeesPOJO);

                Integer companyId = ed.getCompanyId();

                //添加公司分
                synchronized (this){
                    Float companyScopes = companyScope.getOrDefault(companyId, new Float(0))+scope;
                    companyScope.put(companyId, companyScopes);
                }
            });
        }
        exr1.shutdown();
        exr1.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        //公司处理线程池
        ExecutorService exr2 = Executors.newCachedThreadPool();
        companyScope.forEach((x, y) -> {
            exr2.submit(() -> {
                CompanyDetails cd = companyDetailsService.getById(x);
                CompanyDetailsPOJO cdp = new CompanyDetailsPOJO();
                cdp.setCompanyId(x);
                cdp.setCompanyName(cd.getNoCertifiedCompany());
                cdp.setLogoUrl(cd.getLogoUrl());
                cdp.setCertified(cd.getIsValidate());
                cdp.setCompanyProfile(cd.getCompanyProfile());
                cdp.setAddress(cd.getAddress1()+cd.getAddress2()+cd.getAddress3()+cd.getAddress4());
                CompanyPOJO cp = new CompanyPOJO();
                cp.setScope(y);
                cp.setCompanyDetailsPOJO(cdp);
                companyPOJOS.add(cp);
            });
        });
        exr2.shutdown();
        exr2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        SortListUtil<EmployeesPOJO> sort1 = new SortListUtil<>();
        sort1.SortByFloat(employeesPOJOS, "getScope", "desc");
        SortListUtil<CompanyPOJO> sort2 = new SortListUtil<>();
        sort2.SortByFloat(companyPOJOS, "getScope", "desc");

        Map<String, Integer> number = sysConfigService.getNumber();
        Integer a = number.get(ApplicationConfigConstants.numberOfConsecutiveEmployeesInteger);
        Integer b = number.get(ApplicationConfigConstants.numberOfConsecutiveCompanyInteger);
        Integer sum = a + b;
        Integer count = employeesPOJOS.size() + companyPOJOS.size();
        for (int i = 0; i < count ; i++) {
            Integer exist = i%sum;
            IndexResultPOJO indexResultPOJO = null;
            if (employeesPOJOS.size() == 0 && companyPOJOS.size() != 0){
                //公司
                indexResultPOJO = new IndexResultPOJO(companyPOJOS.remove(0));
                resultPOJOS.add(indexResultPOJO);
                continue;
            }
            if (companyPOJOS.size() == 0 && employeesPOJOS.size() != 0){
                //保洁
                indexResultPOJO = new IndexResultPOJO(employeesPOJOS.remove(0));
                resultPOJOS.add(indexResultPOJO);
                continue;
            }
            if (0 <= exist && exist < a){
                //保洁
                indexResultPOJO = new IndexResultPOJO(employeesPOJOS.remove(0));
                resultPOJOS.add(indexResultPOJO);
                continue;
            }
            if (a <= exist && exist < sum){
                //公司
                indexResultPOJO = new IndexResultPOJO(companyPOJOS.remove(0));
                resultPOJOS.add(indexResultPOJO);
                continue;
            }
        }


        return R.ok(resultPOJOS, "搜索成功");
    }

    /** 参数判空 */
    private List<String> paramsEmpty(QueryDTO dto){
        /***
         * 判空
         */
        Integer indexId = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getIndexId).get();
        List<Integer> jobs = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getJobs).get();
        Integer type = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getType).get();
        LocalDate start = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getStart).get();
        LocalDate end = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getEnd).get();
        List<TimeSlot> timeSlots = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getTimeSlots).get();
        BigDecimal lowHourlyWage = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getLowHourlyWage).get();
        BigDecimal highHourlyWage = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getHighHourlyWage).get();
        AddressDetailsDTO addressDetailsDTO = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getAddressDetails).get();
        Integer priorityType = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getPriorityType).get();
        List<Boolean> certified = OptionalBean.ofNullable(dto)
                .getBean(QueryDTO::getCertified).get();
        String toCode = "TWD";

        List<String> resFailed = new ArrayList<>();
        if (CommonUtils.isEmpty(indexId)) resFailed.add("元素_id為空");
        if (CommonUtils.isEmpty(jobs)) resFailed.add("元素_jobs為空");
        if (CommonUtils.isEmpty(type)) resFailed.add("元素_type為空");
        if (CommonUtils.isEmpty(start)) resFailed.add("元素_start為空");
        if (CommonUtils.isEmpty(end)) resFailed.add("元素_end為空");
        if (CommonUtils.isEmpty(timeSlots)) resFailed.add("元素_timeSlots為空");
        if (CommonUtils.isEmpty(lowHourlyWage)) resFailed.add("元素_lowHourlyWage為空");
        if (CommonUtils.isEmpty(highHourlyWage)) resFailed.add("元素_highHourlyWage為空");
        if (CommonUtils.isEmpty(addressDetailsDTO)) resFailed.add("元素_addressDetailsDTO為空");
        if (CommonUtils.isEmpty(certified)) {
            certified = new ArrayList<>();
            certified.add(true);
            certified.add(true);
        }
        return resFailed;
    }

    /** 搜索池 */
    private List<Integer> searchPool(List<Boolean> certified){
        //鐘點，設置了時間表 設置了工作內容才能被搜索到
        //包工，只要有，就能被搜索到

        List<Integer> searchPool = queryMapper.enableWork();//能干事的
        if (!certified.get(0) && certified.equals(1)){
            //false true 未认证的才要
            List<Integer> authNo = queryMapper.authNo();
            searchPool.retainAll(authNo);
        }
        if (certified.get(0) && !certified.equals(1)){
            //true false 已认证公司才要
            List<Integer> authOk = queryMapper.authOk();
            searchPool.retainAll(authOk);
        }

        return searchPool;
    }

    /** 钟点工時間匹配率 */
    private Float variable1(Map<LocalDate, TodayDetailsPOJO> calendarFreeTime, QueryDTO dto){
        if (CommonUtils.isEmpty(calendarFreeTime)) return new Float(0);
        AtomicReference<Float> h = new AtomicReference<>(new Float(0));      //一共需要幾個小時
        AtomicReference<Float> dailyH = new AtomicReference<>(new Float(0)); //每天需要幾個小時
        Map<LocalDate, List<LocalTime>> freeTime = new HashMap<>(); //空闲時間切片
        List<LocalTime> dailyTimes = new ArrayList<>(); //每日需求時間切片

        dto.getTimeSlots().forEach(timeSlot -> {
            LocalTime start = timeSlot.getTimeSlotStart();
            Float length = timeSlot.getTimeSlotLength();
            Float total = length / 0.5f;
            for (Float i = 0f; i < total; i++) {
                dailyTimes.add(start);
                start = start.plusMinutes(30);
            }
            dailyH.updateAndGet(v -> v + length);
        });
        calendarFreeTime.forEach((x, y) -> {
            List<TimeSlotPOJO> times = y.getTimes();
            List<LocalTime> res = new ArrayList<>();
            times.forEach(time -> {
                LocalTime start = time.getTimeSlotStart();
                Float length = time.getTimeSlotLength();
                Float total = length / 0.5f;
                for (Float i = 0f; i < total; i++) {
                    res.add(start);
                    start = start.plusMinutes(30);
                }
            });
            freeTime.put(x, res);
            h.updateAndGet(v -> v + dailyH.get());
        });

        AtomicReference<Float> okH = new AtomicReference<>(new Float(0));
        freeTime.forEach((x, y) -> {
            //找到y和dailyTimes的交集
            y.retainAll(dailyTimes);
            Float todayOkH = new Float(y.size()) * new Float(0.5);
            okH.updateAndGet(v -> v + todayOkH);
        });

        Float variable1 = new Float(0);
        variable1 = okH.get() / h.get();


        return variable1;
    }

    /** 钟点工工作内容匹配率 */
    private Float variable2(String jobs, QueryDTO dto){
        if (CommonUtils.isEmpty(jobs)) return new Float(0);
        List<Integer> enableJobIds = CommonUtils.stringToList(jobs);
        List<Integer> needJobIds = dto.getJobs();
        enableJobIds.retainAll(needJobIds);
        Float variable2 = new Float(enableJobIds.size()) / new Float(needJobIds.size());
        return variable2;
    }

    /** 包工工作内容匹配率 */
    private Float variable4(List<EmployeesContract> ecs, QueryDTO dto){
        AtomicReference<Float> variable4 = new AtomicReference<>(new Float(0));
        ecs.forEach(ec -> {
            Float variable = variable2(ec.getJobs(), dto);
            variable4.set(variable< variable4.get() ? variable:variable4.get());
        });
        return variable4.get();
    }

    /** 最低时薪 */
    private BigDecimal variable5(List<EmployeesContract> ecs1, List<EmployeesCalendar> ecs2){
        AtomicReference<Float> hourlyWage2 = new AtomicReference<>(Float.MAX_VALUE);
        ecs2.forEach(ec -> {
            Float now = ec.getHourlyWage().floatValue();
            if (now< hourlyWage2.get()) hourlyWage2.set(now);
        });
        if (CommonUtils.isEmpty(ecs2)) {
            AtomicReference<Float> hourlyWage1 = new AtomicReference<>(Float.MAX_VALUE);
            ecs1.forEach(ec -> {
                Float now = ec.getTotalPrice().floatValue()
                        / (new Float(ec.getDateLength()) * ec.getTimeLength());
                if (now< hourlyWage1.get()) hourlyWage1.set(now);
            });
            return new BigDecimal(String.valueOf(hourlyWage1)).setScale(0, BigDecimal.ROUND_DOWN);
        }
        return new BigDecimal(String.valueOf(hourlyWage2)).setScale(0, BigDecimal.ROUND_DOWN);
    }

    /** 距离 */
    private String variable6(EmployeesDetails ed, QueryDTO dto){
        /** instance: 距离准备、筛选 */
        String str = CommonUtils.getInstanceByPoint(ed.getLat(), ed.getLng(), dto.getAddressDetails().getLat().toString(), dto.getAddressDetails().getLng().toString());
        BigDecimal bd = new BigDecimal(str).setScale(2, BigDecimal.ROUND_DOWN);
        return bd.toString();
    }

    /** 评价星级 */
    private Float variable7(EmployeesDetails ed){
        return ed.getStarRating();
    }

    /** 是否推广 */
    private Boolean variable8(Integer employeesId){
        Boolean extensionIsOk = false;
        QueryWrapper qw2 = new QueryWrapper();
        qw2.gt("end_time", LocalDateTime.now());
        qw2.eq("employees_id", employeesId);
        EmployeesPromotion res = employeesPromotionService.getOne(qw2);
        if (CommonUtils.isNotEmpty(res)){
            extensionIsOk = true;
        }
        return extensionIsOk;
    }

}
