package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyWorkListDTO;
import com.housekeeping.admin.dto.CompanyWorkListQueryDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CompanyWorkListMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    @Override
    public R addToTheWorkList(CompanyWorkListDTO companyWorkListDTO) {
        if (CommonUtils.isNotEmpty(companyWorkListDTO.getGroupId())
                && CommonUtils.isNotEmpty(companyWorkListDTO.getOrderId())){
            CompanyWorkList companyWorkList = new CompanyWorkList();
            companyWorkList.setGroupId(companyWorkListDTO.getGroupId());
            companyWorkList.setOrderId(companyWorkList.getOrderId());
            companyWorkList.setCreateTime(LocalDateTime.now());
            companyWorkList.setLastReviserId(TokenUtils.getCurrentUserId());
            return R.ok(baseMapper.insert(companyWorkList), "添加成功");
        }else {
            return R.failed("傳參錯誤,不能為空");
        }
    }

    @Override
    public R page(IPage<CompanyWorkList> page, CompanyWorkListQueryDTO companyWorkListQueryDTO) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getGroupId())){
            queryWrapper.eq("group_id", companyWorkListQueryDTO.getGroupId());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getOrderId())){
            queryWrapper.eq("order_id", companyWorkListQueryDTO.getOrderId());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getCreateTimeStart())){
            queryWrapper.ge("create_time", companyWorkListQueryDTO.getCreateTimeStart());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getCreateTimeEnd())){
            queryWrapper.le("create_time", companyWorkListQueryDTO.getCreateTimeEnd());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getLastReviserId())){
            queryWrapper.eq("last_reviser_id", companyWorkListQueryDTO.getLastReviserId());
        }
        return R.ok(baseMapper.selectPage(page, queryWrapper));
    }

    /***
     * 匹配可以做订单的员工 :: 只能通过经理来做
     * @param orderId
     * @return
     */
    @Override
    public R matchTheOrder(Integer orderId) {

        /** 1.獲取我的所有組裡面的所有員工 */
        Integer managerId = TokenUtils.getCurrentUserId();
        List<GroupEmployees> groupEmployeesList = (List<GroupEmployees>) groupEmployeesService.matchTheEmployees(managerId).getData();
        List<Integer> employeesList = (List<Integer>) groupEmployeesList.stream().map(x -> {
            return x.getEmployeesId();
        }).collect(Collectors.toList());
        /* 獲取我的所有組裡面的所有員工 */

        //詳細訂單計劃
        List<SysOrderPlan> sysOrderPlanList = (List<SysOrderPlan>) sysOrderPlanService.getAllByOrderId(orderId).getData();

        //返回结果集
        Map<Object, Object> resMap = new HashMap<>();

        /** 2.員工篩選，得到能做訂單的，詳細訂單計劃滿足日程表規範，並且與員工工作表不衝突 */
        List<Integer> filtered = employeesList.stream().filter(x -> {
            //返回結果子集
            List<Map<String, Object>> res = new ArrayList<>();
            Boolean ok = true;
            /** 2.1判斷該員工合不合適 */
            //員工日程表
            List<EmployeesCalendar> employeesCalendarList = (List<EmployeesCalendar>) employeesCalendarService.getCalendarByEmployees(x).getData();
            //員工工作表計劃
            List<EmployeesWorksheetPlan> employeesWorksheetPlans = (List<EmployeesWorksheetPlan>) employeesWorksheetPlanService.getWorkSheetPlanByEmployeesId(x).getData();

            /** 2.1.1 不滿足日程表的員工检索 */
            List<SysOrderPlan> sysOrderPlanListByCalendar = sysOrderPlanList;
            List<SysOrderPlan> sysOrderPlanListByCalendar2 = sysOrderPlanList;
            Map<Boolean, List<EmployeesCalendar>> map= new HashMap<>();
            employeesCalendarList.forEach(y -> {
                List<EmployeesCalendar> te = map.getOrDefault(y.getStander(), new ArrayList<>());
                te.add(y);
                map.put(y.getStander(), te);
            });
            //特殊日期規則判斷
            if (map.containsKey(false)){
                /**
                 * 先将特殊日期的按日期形成HashMap
                 */
                Map<LocalDate, List<PeriodOfTime>> map1 = new HashMap<>();
                map.get(false).forEach(dateRule -> {
                    List list = map1.getOrDefault(dateRule.getData(), new ArrayList<>());
                    list.add(new PeriodOfTime(dateRule.getTimeSlotStart(), dateRule.getTimeSlotLength()));
                    map1.put(dateRule.getData(), list);
                });
                sysOrderPlanListByCalendar2 = sysOrderPlanListByCalendar.stream().filter(y -> {
                    AtomicReference<Boolean> keepFlag = new AtomicReference<>(true);
                    AtomicReference<Boolean> couldYouArrange = new AtomicReference<>(true);
                    if (map1.containsKey(y.getDate())){
                        //删掉订单里面的该记录，不用进行后续判断了，以这个为主
                        keepFlag.set(false);
                        couldYouArrange.set(false);
                        PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength()); //订单的时间段
                        List<PeriodOfTime> periodOfTimesList = map1.get(y.getDate()); //日程表的时间段：特殊日期
                        periodOfTimesList.forEach(z -> {
                            PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength()); //日程表的时间段
                            if (CommonUtils.periodOfTimeAContainsPeriodOfTimeB(periodOfTime2, periodOfTime1)){
//                                //如果periodOfTime1 <= periodOfTime2 被包含，那麼證明可以包含
                                couldYouArrange.set(true);
                            }
                        });
                    }
                    if (!couldYouArrange.get()){
                        //如果不能被包含那麼就需要上傳到結果集
                        Map<String, Object> entity = new HashMap<>();
                        entity.put("date", y.getDate());
                        PeriodOfTime periodOfTime = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                        entity.put("sysOrderPlan", periodOfTime);
                        entity.put("message", "該工作段無法被安排，因為與員工設置的日期的時間表不符合");
                        res.add(entity);
                    }
                    return keepFlag.get();//该日期如果判斷過了，就刪掉因為不需要參與周獲通用時間表判斷了
                }).collect(Collectors.toList());
            }
            //判斷訂單計劃的每天的每個時間段能否屬於一個時間段
            if (map.containsKey(true)){
                /**
                 * 先将周日程表的按星期几形成HashMap
                 */
                Map<Integer, List<PeriodOfTime>> map2 = new HashMap<>();
                map.get(true).forEach(weekRule -> {
                    String weekString = weekRule.getWeek();
                    for (int i = 0; i < weekString.length(); i++) {
                        Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                        List list = map2.getOrDefault(weekInteger, new ArrayList<>());
                        list.add(new PeriodOfTime(weekRule.getTimeSlotStart(), weekRule.getTimeSlotLength()));
                        map2.put(weekInteger, list);
                    }
                });
                sysOrderPlanListByCalendar2.forEach(y -> {//遍歷訂單計劃
                    AtomicReference<Boolean> couldYouArrange = new AtomicReference<>(false); //默认不给过
                    if (map2.containsKey(y.getDate().getDayOfWeek().getValue())){
                        PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength()); //订单的时间段
                        List<PeriodOfTime> periodOfTimesList = map2.get(y.getDate().getDayOfWeek().getValue()); //周日程表的时间段
                        periodOfTimesList.forEach(z -> {
                            PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength()); //日程表的时间段
                            if (CommonUtils.periodOfTimeAContainsPeriodOfTimeB(periodOfTime2, periodOfTime1)){
//                                //如果periodOfTime1 <= periodOfTime2 被包含，那麼證明可以包含
                                couldYouArrange.set(true);
                            }
                        });
                    }
                    if (!couldYouArrange.get()){
                        //如果不能被包含那麼就需要上傳到結果集
                        Map<String, Object> entity = new HashMap<>();
                        entity.put("date", y.getDate());
                        PeriodOfTime periodOfTime = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                        entity.put("sysOrderPlan", periodOfTime);
                        entity.put("message", "該工作段無法被安排，因為與員工設置的周時間表不符合");
                        res.add(entity);
                    }
                });
            }else {
                //如果沒有true存在，那麼“”默認的規則就需要生效
                if (map.containsKey("")){
                    sysOrderPlanListByCalendar2.forEach(y -> {
                        AtomicReference<Boolean> couldYouArrange = new AtomicReference<>(false);
                        List<EmployeesCalendar> dateRules = map.get("");
                        PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                        dateRules.forEach(z -> {
                            PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                            if (CommonUtils.periodOfTimeAContainsPeriodOfTimeB(periodOfTime2, periodOfTime1)){
                                //如果periodOfTime1 <= periodOfTime2 被包含，那麼證明可以包含
                                couldYouArrange.set(true);
                            }
                        });
                        if (!couldYouArrange.get()){
                            //如果不能被包含那麼就需要上傳到結果集
                            Map<String, Object> entity = new HashMap<>();
                            entity.put("date", y.getDate());
                            PeriodOfTime periodOfTime = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                            entity.put("sysOrderPlan", periodOfTime);
                            entity.put("message", "該工作段無法被安排，與員工設置的擠出時間表不符合");
                            res.add(entity);
                        }
                    });
                }else {
                    Map<String, Object> entity = new HashMap<>();
                    entity.put("message", "用戶沒有設置通用日程表也沒有添加任何周日程計劃");
                    res.add(entity);
                }
            }
            /* 不滿足日程表的員工检索 */


            /** 2.1.2 工作表衝突的員工检索 */
            List<SysOrderPlan> sysOrderPlanListByWorksheetPlans = sysOrderPlanList;
            sysOrderPlanListByWorksheetPlans.forEach(y -> {
                PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                employeesWorksheetPlans.forEach(z -> {
                   if (y.getDate().equals(z.getData())){
                       PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                       if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                           Map<String, Object> entity = new HashMap<>();
                           entity.put("date", y.getDate());
                           entity.put("sysOrderPlan", periodOfTime1);
                           entity.put("worksheetPlans", periodOfTime2);
                           entity.put("message", "與現有的工作表工作有衝突");
                           res.add(entity);
                       }
                   }
               });
            });
            /* 排除工作表衝突的員工检索 */

            if (!res.isEmpty()){
                ok = false;
                resMap.put(x, res);
            }
            /* 判斷該員工合不合適 */
            return ok;
        }).collect(Collectors.toList());
        /* 員工篩選，得到能做訂單的 */

        Map lastMap = new HashMap();
        lastMap.put("don't match", resMap);
        lastMap.put("matching", filtered);
        return R.ok(lastMap, "成功獲取可以接單的員工id,以及不能接单员工的冲突情况");
    }

    @Override
    public R dispatchOrder(Integer orderId, Integer employeesId) {

        return R.ok();
    }
}
