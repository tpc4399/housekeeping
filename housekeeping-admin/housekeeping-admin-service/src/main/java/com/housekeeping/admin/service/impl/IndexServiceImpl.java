package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.IndexMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.entity.PeriodOfTimeWithHourlyWage;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.OptionalBean;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service("indexService")
public class IndexServiceImpl extends ServiceImpl<IndexMapper, Index> implements IIndexService {

    @Resource
    private ISysJobContendService jobContendService;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesJobsService employeesJobsService;
    @Resource
    private ICompanyPromotionService companyPromotionService;
    @Resource
    private IEmployeesPromotionService employeesPromotionService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ICustomerAddressService customerAddressService;
    @Resource
    private ICurrencyService currencyService;
    @Resource
    private IAddressCodingService addressCodingService;

    @Override
    public R getCusById(Integer id) {
        List<Integer> ids = baseMapper.getContentIds(id);
        ArrayList<SysJobContend> sysJobContends = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            SysJobContend byId = jobContendService.getById(ids.get(i));
            sysJobContends.add(byId);
        }
        return R.ok(sysJobContends);
    }

    @Override
    public R query(IndexQueryDTO indexQueryDTO) {

        /***
         * 判空
         */
        Integer indexId = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getIndexId).get();
        LocalDate date = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getDate).get();
        List<TimeSlot> timeSlotList = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getTimeSlotList).get();
        String code = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getCode).get();
        String lowestPrice = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getLowestPrice).get();
        String highestPrice = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getHighestPrice).get();
        Integer addressId = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getAddressId).get();

        List<String> resFailed = new ArrayList<>();
        if (CommonUtils.isEmpty(indexId)) resFailed.add("元素_id為空");
        if (CommonUtils.isEmpty(date)) resFailed.add("服務日期為空");
        if (CommonUtils.isEmpty(timeSlotList)) resFailed.add("上門時間段為空");
        if (CommonUtils.isEmpty(code)) resFailed.add("貨幣代碼為空");
        if (CommonUtils.isEmpty(lowestPrice)) resFailed.add("最低價為空");
        if (CommonUtils.isEmpty(highestPrice)) resFailed.add("最高價為空");
        if (CommonUtils.isEmpty(addressId)) resFailed.add("服務地址為空");
        if (!resFailed.isEmpty()){
            return R.failed(resFailed, "存在空值");
        }

        /** 返回结果 */
        Map<String, Object> map = new HashMap<>();
        /** searchPool 员工搜索池：只有符合条件的保洁员才会放进池子（设定了工作内容，设定了工作时间表） */
        List<Integer> searchPool = new ArrayList<>();
        QueryWrapper qw1 = new QueryWrapper();
        qw1.select("employees_id").groupBy("employees_id");
        List<Integer> employeeIdsFromCalendar = employeesCalendarService.listObjs(qw1);
        List<Integer> employeeIdsFromJob = employeesJobsService.listObjs(qw1);
        searchPool = getIntersection(employeeIdsFromCalendar, employeeIdsFromJob);
        /** promoteCompanyIds 推广公司搜索池 */
        List<Integer> promoteCompanyIds = new ArrayList<>();
        QueryWrapper qw2 = new QueryWrapper();
        qw2.select("company_id").gt("end_time", LocalDateTime.now());
        promoteCompanyIds = companyPromotionService.listObjs(qw2);
        /** promoteEmployeeIds 推广员工搜索池 */
        List<Integer> promoteEmployeeIds = new ArrayList<>();
        QueryWrapper qw3 = new QueryWrapper();
        qw3.select("employees_id").gt("end_time", LocalDateTime.now());
        promoteEmployeeIds = employeesPromotionService.listObjs(qw3);
        /** customerAddress 客戶地址准备 */
        CustomerAddress customerAddress = customerAddressService.getById(addressId);
        /** instanceMap 保洁员距离准备 */
        Map<Integer, Integer> instanceMap = new HashMap<>();
        /** priceMap 保洁员价格准备 */
        Map<Integer, BigDecimal> priceMap = new HashMap<>();
        /** matchingEmployees 服务时间段匹配的员工列表准备 */
        List<Integer> matchingEmployees = new ArrayList<>();

        /**
         * 【推荐公司】  1、公司推广列表里面的公司 2、公司手底下有保洁员被匹配（时间段，工作内容）
         * 【推荐保洁员】1、员工推广列表里面的员工 2、员工可以被匹配（时间段，工作内容），按价格接近度排序
         * 【附近保洁员】1、匹配到的员工，按距离排序
         * 【最佳保洁员】1、匹配到的员工，按评分排序
         */

        searchPool.forEach(employeesId -> {
            /** existEmployee 当前保洁员信息 */
            EmployeesDetails existEmployee = employeesDetailsService.getById(employeesId);
            /**
             *  calendarMap: 时间表map准备
             *  map1, map2, map3: 没有做相邻时间段合并的,带时薪的，计算价格用
             *  map11,map22,map33:时间段合并了的，时间段匹配用
             */
            QueryWrapper qw4 = new QueryWrapper();
            qw4.eq("employees_id", employeesId);
            List<EmployeesCalendar> employeesCalendarList = employeesCalendarService.list(qw4);
            Map<Object, List<EmployeesCalendar>> calendarMap = new HashMap<>();
            List<EmployeesCalendar> a = new ArrayList<>();
            List<EmployeesCalendar> b = new ArrayList<>();
            List<EmployeesCalendar> c = new ArrayList<>();
            employeesCalendarList.forEach(x -> {
                if (x.getStander() == null) c.add(x);
                else if (x.getStander()) a.add(x);
                else b.add(x);
            });
            calendarMap.put(true,a);
            calendarMap.put(false, b);
            calendarMap.put(null, c);
            Map<LocalDate, List<PeriodOfTimeWithHourlyWage>> map1 = this.getMap1(calendarMap, code);
            Map<Integer, List<PeriodOfTimeWithHourlyWage>> map2 = this.getMap2(calendarMap, code);
            Map<String, List<PeriodOfTimeWithHourlyWage>> map3 = this.getMap3(calendarMap, code);
            Map<LocalDate, List<PeriodOfTime>> map11 = this.getMap11(calendarMap);
            Map<Integer, List<PeriodOfTime>> map22 = this.getMap22(calendarMap);
            Map<String, List<PeriodOfTime>> map33 = this.getMap33(calendarMap);

            /**
             * 1、时段匹配，员工筛选，硬性条件,需要使用到时间表calendarMap
             * 2、xPoWage 时间段对应价格准备
             */
            List<PeriodOfTimeWithHourlyWage> xPoWage = new ArrayList<>();

            AtomicReference<Boolean> isOk = new AtomicReference<>(true);
            timeSlotList.forEach(x -> {
                AtomicReference<Boolean> existTimeSlotIsOk = new AtomicReference<>(false);
                PeriodOfTime xPo = new PeriodOfTime(x.getTimeSlotStart(), x.getTimeSlotLength());
                if (map11.containsKey(date)){
                    map11.get(date).forEach(y -> {
                        if (CommonUtils.periodOfTimeAContainsPeriodOfTimeB(y, xPo)){
                            existTimeSlotIsOk.set(true);
                            //计算价格
                            map1.get(date).forEach(z -> {
                                PeriodOfTime zPo = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                                if (CommonUtils.doRechecking(xPo, zPo)){
                                    PeriodOfTime common = PeriodOfTime.getIntersection(xPo, zPo);
                                    PeriodOfTimeWithHourlyWage period = new PeriodOfTimeWithHourlyWage(
                                            common.getTimeSlotStart(),
                                            common.getTimeSlotLength(),
                                            z.getHourlyWage()
                                    );
                                    xPoWage.add(period);
                                }
                            });
                        }
                    });
                }else if (map22.containsKey(date.getDayOfWeek().getValue())){
                    map22.get(date.getDayOfWeek().getValue()).forEach(y -> {
                        if (CommonUtils.periodOfTimeAContainsPeriodOfTimeB(y, xPo)){
                            existTimeSlotIsOk.set(true);
                            //计算价格
                            map2.get(date.getDayOfWeek().getValue()).forEach(z -> {
                                PeriodOfTime zPo = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                                if (CommonUtils.doRechecking(xPo, zPo)){
                                    PeriodOfTime common = PeriodOfTime.getIntersection(xPo, zPo);
                                    PeriodOfTimeWithHourlyWage period = new PeriodOfTimeWithHourlyWage(
                                            common.getTimeSlotStart(),
                                            common.getTimeSlotLength(),
                                            z.getHourlyWage()
                                    );
                                    xPoWage.add(period);
                                }
                            });
                        }
                    });
                }else {
                    map33.get(null).forEach(y -> {
                        if (CommonUtils.periodOfTimeAContainsPeriodOfTimeB(y, xPo)){
                            existTimeSlotIsOk.set(true);
                            //计算价格
                            map3.get(null).forEach(z -> {
                                PeriodOfTime zPo = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                                if (CommonUtils.doRechecking(xPo, zPo)){
                                    PeriodOfTime common = PeriodOfTime.getIntersection(xPo, zPo);
                                    PeriodOfTimeWithHourlyWage period = new PeriodOfTimeWithHourlyWage(
                                            common.getTimeSlotStart(),
                                            common.getTimeSlotLength(),
                                            z.getHourlyWage()
                                    );
                                    xPoWage.add(period);
                                }
                            });
                        }
                    });
                }
                if (!existTimeSlotIsOk.get()){
                    isOk.set(false);
                }
            });
            if (isOk.get()){
                matchingEmployees.add(employeesId);
            }else {
                return;
            }

            /** jobContendIds: 工作内容准备 */
            QueryWrapper qw5 = new QueryWrapper();
            qw5.select("job_id").eq("employees_id", employeesId);
            List<Integer> jobContendIds = employeesJobsService.listObjs(qw5);

            /** instance: 距离准备 */
            Integer instance = addressCodingService.getInstanceByPointByWalking(existEmployee.getLng(),
                    existEmployee.getLat(),
                    customerAddress.getLng(),
                    customerAddress.getLat()
            );
            instanceMap.put(employeesId, instance);

            /** price: 价格准备 */
            AtomicReference<BigDecimal> bo = new AtomicReference<>(new BigDecimal(0));
            xPoWage.forEach(x -> {
                bo.set(bo.get().add(x.getHourlyWage()));
            });
            BigDecimal price = bo.get();

            /** score: 评分(星级)准备 */
            Float score = existEmployee.getStarRating();

            /** companyId: 所属公司准备 */
            Integer companyId = existEmployee.getCompanyId();



        });
        return null;
    }

    private List<Integer> getIntersection(List<Integer> a, List<Integer> b){
        a.retainAll(b);
        return a;
    }

    private Map<LocalDate, List<PeriodOfTimeWithHourlyWage>> getMap1(Map<Object, List<EmployeesCalendar>> calendarMap, String toCode){
        Map<LocalDate, List<PeriodOfTimeWithHourlyWage>> map1 = new HashMap<>();
        calendarMap.get(false).forEach(dateRule -> {
            List list1 = map1.getOrDefault(dateRule.getData(), new ArrayList<>());
            BigDecimal hourlyWage = currencyService.exchangeRateToBigDecimal(dateRule.getCode(), toCode, dateRule.getHourlyWage());
            list1.add(new PeriodOfTimeWithHourlyWage(dateRule.getTimeSlotStart(), dateRule.getTimeSlotLength(), hourlyWage));
            map1.put(dateRule.getData(), list1);
        });
        return map1;
    }

    private Map<Integer, List<PeriodOfTimeWithHourlyWage>> getMap2(Map<Object, List<EmployeesCalendar>> calendarMap, String toCode){
        Map<Integer, List<PeriodOfTimeWithHourlyWage>> map2 = new HashMap<>();
        calendarMap.get(true).forEach(weekRule -> {
            String weekString = weekRule.getWeek();
            for (int i = 0; i < weekString.length(); i++) {
                Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                List list = map2.getOrDefault(weekInteger, new ArrayList<>());
                BigDecimal hourlyWage = currencyService.exchangeRateToBigDecimal(weekRule.getCode(), toCode, weekRule.getHourlyWage());
                list.add(new PeriodOfTimeWithHourlyWage(weekRule.getTimeSlotStart(), weekRule.getTimeSlotLength(), hourlyWage));
                map2.put(weekInteger, list);
            }
        });
        return map2;
    }

    private Map<String, List<PeriodOfTimeWithHourlyWage>> getMap3(Map<Object, List<EmployeesCalendar>> calendarMap, String toCode){
        Map<String, List<PeriodOfTimeWithHourlyWage>> map3 = new HashMap<>();
        calendarMap.get(null).forEach(x -> {
            List list = map3.getOrDefault(null, new ArrayList<>());
            BigDecimal hourlyWage = currencyService.exchangeRateToBigDecimal(x.getCode(), toCode, x.getHourlyWage());
            list.add(new PeriodOfTimeWithHourlyWage(x.getTimeSlotStart(), x.getTimeSlotLength(), hourlyWage));
            map3.put(null, list);
        });
        return map3;
    }

    private Map<LocalDate, List<PeriodOfTime>> getMap11(Map<Object, List<EmployeesCalendar>> calendarMap){
        Map<LocalDate, List<PeriodOfTime>> map1 = new HashMap<>();
        Map<LocalDate, List<PeriodOfTime>> map11 = new HashMap<>();
        calendarMap.get(false).forEach(dateRule -> {
            List list1 = map1.getOrDefault(dateRule.getData(), new ArrayList<>());
            list1.add(new PeriodOfTime(dateRule.getTimeSlotStart(), dateRule.getTimeSlotLength()));
            map1.put(dateRule.getData(), list1);
        });
        map1.forEach((date, list) -> {
            List<PeriodOfTime> periodOfTimes = list;
            if (list.size() == 0 || list.size() == 1){
                //什么也不做
            }else {
                for (int i = list.size() - 1; i > 0; i--) {
                    LocalTime xStart = list.get(i).getTimeSlotStart();
                    LocalTime xEnd = list.get(i).getTimeSlotStart()
                            .plusHours((int) (list.get(i).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i).getTimeSlotLength()%1)* 60));
                    LocalTime yStart = list.get(i-1).getTimeSlotStart();
                    LocalTime yEnd = list.get(i-1).getTimeSlotStart()
                            .plusHours((int) (list.get(i-1).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i-1).getTimeSlotLength()%1)* 60));
                    if (xEnd.equals(yStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(xStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.add(z);
                    }else if (yEnd.equals(xStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(yStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else {
                        //没检测到相邻的，啥也不干
                    }
                }
            }
            map11.put(date, periodOfTimes);
        });
        return map11;
    }

    private Map<Integer, List<PeriodOfTime>> getMap22(Map<Object, List<EmployeesCalendar>> calendarMap){
        Map<Integer, List<PeriodOfTime>> map2 = new HashMap<>();
        Map<Integer, List<PeriodOfTime>> map22 = new HashMap<>();
        calendarMap.get(true).forEach(weekRule -> {
            String weekString = weekRule.getWeek();
            for (int i = 0; i < weekString.length(); i++) {
                Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                List list = map2.getOrDefault(weekInteger, new ArrayList<>());
                list.add(new PeriodOfTime(weekRule.getTimeSlotStart(), weekRule.getTimeSlotLength()));
                map2.put(weekInteger, list);
            }
        });
        map2.forEach((week, list) -> {
            List<PeriodOfTime> periodOfTimes = list;
            if (list.size() == 0 || list.size() == 1){
                //什么也不做
            }else {
                for (int i = list.size() - 1; i > 0; i--) {
                    LocalTime xStart = list.get(i).getTimeSlotStart();
                    LocalTime xEnd = list.get(i).getTimeSlotStart()
                            .plusHours((int) (list.get(i).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i).getTimeSlotLength()%1)* 60));
                    LocalTime yStart = list.get(i-1).getTimeSlotStart();
                    LocalTime yEnd = list.get(i-1).getTimeSlotStart()
                            .plusHours((int) (list.get(i-1).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i-1).getTimeSlotLength()%1)* 60));
                    if (xEnd.equals(yStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(xStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else if (yEnd.equals(xStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(yStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else {
                        //没检测到相邻的，啥也不干
                    }
                }
            }
            map22.put(week, periodOfTimes);
        });
        return map22;
    }

    private Map<String, List<PeriodOfTime>> getMap33(Map<Object, List<EmployeesCalendar>> calendarMap){
        Map<String, List<PeriodOfTime>> map3 = new HashMap<>();
        Map<String, List<PeriodOfTime>> map33 = new HashMap<>();
        calendarMap.get(null).forEach(x -> {
            List list = map3.getOrDefault(null, new ArrayList<>());
            list.add(new PeriodOfTime(x.getTimeSlotStart(), x.getTimeSlotLength()));
            map3.put(null, list);
        });
        map3.forEach((string, list) -> {
            List<PeriodOfTime> periodOfTimes = list;
            if (list.size() == 0 || list.size() == 1){
                //什么也不做
            }else {
                for (int i = list.size() - 1; i > 0; i--) {
                    LocalTime xStart = list.get(i).getTimeSlotStart();
                    LocalTime xEnd = list.get(i).getTimeSlotStart()
                            .plusHours((int) (list.get(i).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i).getTimeSlotLength()%1)* 60));
                    LocalTime yStart = list.get(i-1).getTimeSlotStart();
                    LocalTime yEnd = list.get(i-1).getTimeSlotStart()
                            .plusHours((int) (list.get(i-1).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i-1).getTimeSlotLength()%1)* 60));
                    if (xEnd.equals(yStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(xStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else if (yEnd.equals(xStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(yStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else {
                        //没检测到相邻的，啥也不干
                    }
                }
            }
            map33.put(string, periodOfTimes);
        });
        return map33;
    }
}
