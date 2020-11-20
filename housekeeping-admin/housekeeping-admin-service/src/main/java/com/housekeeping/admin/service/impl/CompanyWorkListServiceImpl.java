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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
        });
        /* 獲取我的所有組裡面的所有員工 */

        //詳細訂單計劃
        List<SysOrderPlan> sysOrderPlanList = (List<SysOrderPlan>) sysOrderPlanService.getAllByOrderId(orderId).getData();

        //返回结果集
        Map<Object, Object> resMap = new HashMap<>();

        /** 2.員工篩選，得到能做訂單的，詳細訂單計劃滿足日程表規範，並且與員工工作表不衝突 */
        employeesList.stream().filter(x -> {
            //返回結果子集
            List<Map<String, Object>> res = new ArrayList<>();
            Boolean ok = true;
            /** 2.1判斷該員工合不合適 */
            //員工日程表
            List<EmployeesCalendar> employeesCalendarList = (List<EmployeesCalendar>) employeesCalendarService.getCalendarByEmployees(x).getData();
            //員工工作表計劃
            List<EmployeesWorksheetPlan> employeesWorksheetPlans = (List<EmployeesWorksheetPlan>) employeesWorksheetPlanService.getWorkSheetPlanByEmployeesId(x);

            /** 2.1.1 不滿足日程表的員工检索 */
            List<SysOrderPlan> sysOrderPlanListByCalendar = sysOrderPlanList;
            Map<Boolean, List<EmployeesCalendar>> map= new HashMap<>();
            employeesCalendarList.forEach(y -> {
                List<EmployeesCalendar> te = map.getOrDefault(y.getStander(), new ArrayList<>());
                te.add(y);
                map.put(y.getStander(), te);
            });
            //特殊日期規則判斷
            if (map.containsKey(false)){
                sysOrderPlanListByCalendar.stream().filter(y -> {
                    AtomicReference<Boolean> existsFlag = new AtomicReference<>(true);
                    List<EmployeesCalendar> dateRules = map.get(false);
                    dateRules.forEach(z -> {
                        if (y.getDate().equals(z.getData())){
                            existsFlag.set(false);//如果判斷過了，就刪掉
                            PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                            PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                            if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                                Map<String, Object> entity = new HashMap<>();
                                entity.put("date", y.getDate());
                                entity.put("sysOrderPlan", periodOfTime1);
                                entity.put("dateRules", periodOfTime2);
                                res.add(entity);
                            }
                        }
                    });
                    return existsFlag.get();
                });
            }
            if (map.containsKey(true)){
                sysOrderPlanListByCalendar.forEach(y -> {
                    List<EmployeesCalendar> dateRules = map.get(true);
                    dateRules.forEach(z -> {
                        if (z.getWeek().contains(String.valueOf(y.getDate().getDayOfWeek().getValue()))){
                            PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                            PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                            if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                                Map<String, Object> entity = new HashMap<>();
                                entity.put("date", y.getDate());
                                entity.put("sysOrderPlan", periodOfTime1);
                                entity.put("dateRules", periodOfTime2);
                                res.add(entity);
                            }
                        }
                    });
                });
            }else {
                sysOrderPlanListByCalendar.forEach(y -> {
                    List<EmployeesCalendar> dateRules = map.get(true);
                    dateRules.forEach(z -> {
                        PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                        PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                        if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                            Map<String, Object> entity = new HashMap<>();
                            entity.put("date", y.getDate());
                            entity.put("sysOrderPlan", periodOfTime1);
                            entity.put("dateRules", periodOfTime2);
                            res.add(entity);
                        }
                    });
                });
            }
            /* 不滿足日程表的員工检索 */


            /** 2.1.2 工作表衝突的員工检索 */
            List<SysOrderPlan> sysOrderPlanListByWorksheetPlans = sysOrderPlanList;
            sysOrderPlanListByWorksheetPlans.forEach(y -> {
               employeesWorksheetPlans.forEach(z -> {
                   if (y.getDate().equals(z.getData())){
                       PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                       PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                       if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                           Map<String, Object> entity = new HashMap<>();
                           entity.put("date", y.getDate());
                           entity.put("sysOrderPlan", periodOfTime1);
                           entity.put("worksheetPlans", periodOfTime2);
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
        });
        /* 員工篩選，得到能做訂單的 */

        resMap.put("ok", employeesList);
        return R.ok(resMap, "成功獲取可以接單的員工id,以及不能接单员工的冲突情况");
    }

    @Override
    public R dispatchOrder(Integer orderId, Integer employeesId) {

        return R.ok();
    }
}
