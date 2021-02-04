package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.dto.DateSlot;
import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.admin.entity.EmployeesContractDetails;
import com.housekeeping.admin.mapper.EmployeesContractMapper;
import com.housekeeping.admin.service.IEmployeesContractDetailsService;
import com.housekeeping.admin.service.IEmployeesContractService;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.SortListUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/1/30 17:22
 */
@Service("employeesContractService")
public class EmployeesContractServiceImpl
        extends ServiceImpl<EmployeesContractMapper, EmployeesContract>
        implements IEmployeesContractService {

    @Resource
    private IEmployeesContractDetailsService employeesContractDetailsService;

    @Override
    public R add(AddEmployeesContractDTO dto) {
        /* 時間段合理性判斷 */
        List<String> resCollections = rationalityJudgment(dto);//不合理性结果收集
        if (resCollections.size() != 0){
            return R.failed(resCollections, "時間段不合理");
        }

        /* 添加包工業務 */
        EmployeesContract employeesContract = new EmployeesContract(dto);
        List<Integer> activityIds = dto.getActivityIds();
        StringBuilder activityIdsStr = new StringBuilder();
        activityIds.forEach(x->{
            activityIdsStr.append(x);
        });
        employeesContract.setActivityIds(activityIdsStr.toString());
        Integer maxContractId = 0;
        synchronized (this){
            this.save(employeesContract);
            maxContractId = ((EmployeesContract) CommonUtils.getMaxId("employees_contract", this)).getId();
        }
        List<EmployeesContractDetails> employeesContractDetailsList = new ArrayList<>();
        Integer finalMaxContractId = maxContractId;
        dto.getWeekAndTimeSlotsList().forEach(weekAndTimeSlots -> {
            StringBuilder weekStr = new StringBuilder();
            weekAndTimeSlots.getWeek().forEach(week -> {
                weekStr.append(week);
            });
            weekAndTimeSlots.getTimeSlot().forEach(timeSlot -> {
                EmployeesContractDetails employeesContractDetails = new EmployeesContractDetails();
                employeesContractDetails.setContractId(finalMaxContractId);
                employeesContractDetails.setWeek(weekStr.toString());
                employeesContractDetails.setTimeSlotStart(timeSlot.getTimeSlotStart());
                employeesContractDetails.setTimeSlotLength(timeSlot.getTimeSlotLength());
                employeesContractDetailsList.add(employeesContractDetails);
            });
        });
        employeesContractDetailsService.saveBatch(employeesContractDetailsList);
        return R.ok(maxContractId, "添加成功");
    }

    @Override
    public R getByEmployeesId(Integer employeesId) {
        return null;
    }

    @Override
    public Map<LocalDate, List<TimeSlot>> getCalendarByContractId(DateSlot dateSlot, Integer contractId) {
        Map<LocalDate, List<TimeSlot>> res = new HashMap<>();
        SortListUtil<TimeSlot> sort = new SortListUtil<TimeSlot>();
        Map<Integer, List<TimeSlot>> week = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("contract_id", contractId);
        List<EmployeesContractDetails> employeesContractDetails = employeesContractDetailsService.list(qw);
        employeesContractDetails.forEach(employeesContractDetail -> {
            String weekString = employeesContractDetail.getWeek();
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setTimeSlotStart(employeesContractDetail.getTimeSlotStart());
            timeSlot.setTimeSlotLength(employeesContractDetail.getTimeSlotLength());
            for (int i = 0; i < weekString.length(); i++) {
                Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                List<TimeSlot> timeSlots = week.getOrDefault(weekInteger, new ArrayList<>());
                timeSlots.add(timeSlot);
                sort.Sort(timeSlots, "getTimeSlotStart", null);
                week.put(weekInteger, timeSlots);
            }
        });

        LocalDate start = dateSlot.getStart();
        LocalDate end = dateSlot.getEnd();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            res.put(date, week.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>()));
        }
        return res;
    }

    @Override
    public Map<LocalDate, List<TimeSlot>> getFreeTimeByContractId(DateSlot dateSlot, Integer contractId) {
        /* 2021-2-4 暂时先这样写着，目前还没做派任务，所以空闲时间=时间表 */
        return this.getCalendarByContractId(dateSlot, contractId);
    }

    List<String> rationalityJudgment(AddEmployeesContractDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        SortListUtil<TimeSlot> sort = new SortListUtil<TimeSlot>();
        Map<Integer, List<TimeSlot>> map = new HashMap<>();
        dto.getWeekAndTimeSlotsList().forEach(weekAndTimeSlots -> {
            weekAndTimeSlots.getWeek().forEach(week -> {
                weekAndTimeSlots.getTimeSlot().forEach(timeSlot -> {
                    List<TimeSlot> existTimeSlot = map.getOrDefault(week, new ArrayList<>());
                    existTimeSlot.add(timeSlot);
                    map.put(week, existTimeSlot);
                });
            });
        });
        for (int i = 1; i < 8; i++) {
            List<TimeSlot> existSlot = map.get(i);
            sort.Sort(existSlot, "getTimeSlotStart", null);
            for (int j = 0; j < existSlot.size() - 1; j++) {
                PeriodOfTime period1 = new PeriodOfTime(existSlot.get(j).getTimeSlotStart(), existSlot.get(j).getTimeSlotLength());
                PeriodOfTime period2 = new PeriodOfTime(existSlot.get(j+1).getTimeSlotStart(), existSlot.get(j+1).getTimeSlotLength());
                if (CommonUtils.doRechecking(period1, period2)){
                    //重複的處理方式
                    StringBuilder res = new StringBuilder();
                    res.append("周模板存在時間段重複： week ").append(i).append("  ");
                    res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                    res.append("與");
                    res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                    resCollections.add(res.toString());
                }
            }
        }
        return resCollections;
    }
}
