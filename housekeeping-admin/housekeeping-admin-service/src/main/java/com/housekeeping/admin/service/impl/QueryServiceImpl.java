package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.mapper.QueryMapper;
import com.housekeeping.admin.pojo.IndexResultPOJO;
import com.housekeeping.admin.pojo.TimeSlotPOJO;
import com.housekeeping.admin.pojo.TodayDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.OptionalBean;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Override
    public R query(QueryDTO dto) throws InterruptedException {
        List<String> resFailed = paramsEmpty(dto);
        if (CommonUtils.isNotEmpty(resFailed)) return R.failed(resFailed, "存在空值");    //參數判空

        Map<String, String> weight = sysConfigService.getScopeConfig(dto.getPriorityType());  //權重
        List<Integer> searchPool = this.searchPool(dto.getCertified());                       //搜索池
        List<IndexResultPOJO> resultPOJOS = Collections.synchronizedList(new ArrayList<>());  //分數

        ExecutorService exr = Executors.newCachedThreadPool();
        for (int i = 0; i < searchPool.size(); i++) {
            int finalI = i;
            exr.submit(() -> {
                Integer employeesId = searchPool.get(finalI);
                Map<String, String> weight2 = weight;
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
                List<EmployeesContract> ecs = employeesContractService.list(qw);

                //钟点工空闲時間匹配率
                Float variable1 = this.variable1(calendarFreeTime, dto);

                //钟点工工作內容匹配率
                Float variable2 = this.variable2(ed.getPresetJobIds(), dto);

                //包工工作内容匹配率


                //最低時薪

                //距離

                //評價星級
            });
        }
        exr.shutdown();
        exr.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

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

        List<Integer> searchPool = queryMapper.pool();

        searchPool.stream().filter(x -> {
            if (certified.get(0) && certified.get(1)) return true;
            EmployeesDetails ed = employeesDetailsService.getById(x);
            CompanyDetails cd = companyDetailsService.getById(ed.getCompanyId());
            Boolean isValidate = cd.getIsValidate();
            if (certified.get(0) == false && isValidate == true) return false; //过滤掉已认证的保洁员
            if (certified.get(1) == false && isValidate == false) return false; //过滤掉未认证的保洁员
            //正常走不到这儿来

            return true;
        });

        return searchPool;
    }

    /** 時間匹配率 */
    private Float variable1(Map<LocalDate, TodayDetailsPOJO> calendarFreeTime, QueryDTO dto){
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

    /** 工作内容匹配率 */
    private Float variable2(String jobs, QueryDTO dto){
        if (CommonUtils.isEmpty(jobs)) return new Float(0);
        List<Integer> enableJobIds = CommonUtils.stringToList(jobs);
        List<Integer> needJobIds = dto.getJobs();
        enableJobIds.retainAll(needJobIds);
        Float variable2 = new Float(needJobIds.size()) / new Float(needJobIds.size());
        return variable2;
    }

    private Float variable4(List<EmployeesContract> ecs, QueryDTO dto){
        AtomicReference<Float> variable4 = new AtomicReference<>(new Float(0));
        ecs.forEach(ec -> {
            Float variable = variable2(ec.getJobs(), dto);
            variable4.set(variable< variable4.get() ? variable:variable4.get());
        });
        return variable4.get();
    }


}
