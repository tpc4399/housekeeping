package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.admin.entity.EmployeesCalendarDetails;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.EmployeesCalendarMapper;
import com.housekeeping.admin.service.IEmployeesCalendarDetailsService;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.admin.vo.RecommendedEmployeesVo;
import com.housekeeping.admin.vo.TimeSlotVo;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.OptionalBean;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.SortListUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author su
 * @create 2020/11/12 16:22
 */
@Service("employeesCalendarService")
public class EmployeesCalendarServiceImpl extends ServiceImpl<EmployeesCalendarMapper, EmployeesCalendar> implements IEmployeesCalendarService {

    @Resource
    private IEmployeesCalendarDetailsService employeesCalendarDetailsService;

    @Override
    public R setCalendar(SetEmployeesCalendarDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentA(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }
        /* 删掉原有的 */
        QueryWrapper deleteQw = new QueryWrapper();
        deleteQw.eq("employees_id", dto.getEmployeesId());
        deleteQw.eq("stander", "");
        List<EmployeesCalendar> willDeleteList = this.list(deleteQw);
        willDeleteList.forEach(x->{
            QueryWrapper deleteDependency1 = new QueryWrapper();
            deleteDependency1.eq("calendar_id", x.getId());
            employeesCalendarDetailsService.remove(deleteDependency1);//删除依赖
        });
        this.remove(deleteQw);
        /* 添加新的 */
        dto.getTimeSlotList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            null,
                            null,
                            null,
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            Integer maxCalendarId = 0;
            synchronized (this){
                baseMapper.insert(employeesCalendar);
                maxCalendarId = ((EmployeesCalendar) CommonUtils.getMaxId("employees_calendar", this)).getId();
            }
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = new ArrayList<>();
            Integer finalMaxCalendarId = maxCalendarId;
            timeSlot.getJobAndPriceList().forEach(jobAndPrice -> {
                EmployeesCalendarDetails employeesCalendarDetails =
                        new EmployeesCalendarDetails(
                                finalMaxCalendarId,
                                jobAndPrice.getJobId(),
                                jobAndPrice.getPrice(),
                                jobAndPrice.getCode()
                        );
                employeesCalendarDetailsList.add(employeesCalendarDetails);
            });
            employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList);
        });
        return R.ok("設置成功");
    }

    @Override
    public R addCalendarWeek(SetEmployeesCalendarWeekDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentB(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }
        /* 添加新的 */
        StringBuilder week = new StringBuilder();
        dto.getWeek().forEach(wk->{
            week.append(wk);
        });
        dto.getTimeSlotList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            true,
                            null,
                            week.toString(),
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            Integer maxCalendarId = 0;
            synchronized (this){
                baseMapper.insert(employeesCalendar);
                maxCalendarId = ((EmployeesCalendar) CommonUtils.getMaxId("employees_calendar", this)).getId();
            }
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = new ArrayList<>();
            Integer finalMaxCalendarId = maxCalendarId;
            timeSlot.getJobAndPriceList().forEach(jobAndPrice -> {
                EmployeesCalendarDetails employeesCalendarDetails =
                        new EmployeesCalendarDetails(
                                finalMaxCalendarId,
                                jobAndPrice.getJobId(),
                                jobAndPrice.getPrice(),
                                jobAndPrice.getCode()
                        );
                employeesCalendarDetailsList.add(employeesCalendarDetails);
            });
            employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList);
        });
        return R.ok("設置成功");
    }

    @Override
    public R addCalendarDate(SetEmployeesCalendarDateDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentC(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }
        /* 添加新的 */
        dto.getTimeSlotList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            false,
                            dto.getDate(),
                            null,
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            Integer maxCalendarId = 0;
            synchronized (this){
                baseMapper.insert(employeesCalendar);
                maxCalendarId = ((EmployeesCalendar) CommonUtils.getMaxId("employees_calendar", this)).getId();
            }
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = new ArrayList<>();
            Integer finalMaxCalendarId = maxCalendarId;
            timeSlot.getJobAndPriceList().forEach(jobAndPrice -> {
                EmployeesCalendarDetails employeesCalendarDetails =
                        new EmployeesCalendarDetails(
                                finalMaxCalendarId,
                                jobAndPrice.getJobId(),
                                jobAndPrice.getPrice(),
                                jobAndPrice.getCode()
                        );
                employeesCalendarDetailsList.add(employeesCalendarDetails);
            });
            employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList);
        });
        return R.ok("設置成功");
    }

    @Override
    public Map<LocalDate, List<TimeSlotDTO>> getCalendarByDateSlot(DateSlot dateSlot, Integer employeesId) {

        /* 先得到三大map */
        SortListUtil<TimeSlotDTO> sort = new SortListUtil<TimeSlotDTO>();
        Map<LocalDate, List<TimeSlotDTO>> map1 = new HashMap<>();
        Map<Integer, List<TimeSlotDTO>> map2 = new HashMap<>();
        Map<String, List<TimeSlotDTO>> map3 = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        if (CommonUtils.isEmpty(employeesCalendarList)){
            return null;
        }
        employeesCalendarList.forEach(employeesCalendar -> {
            QueryWrapper qw1 = new QueryWrapper();
            qw.eq("calendar_id", employeesCalendar.getId());
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = employeesCalendarDetailsService.list(qw1);
            List<JobAndPriceDTO> jobAndPriceDTOList = employeesCalendarDetailsList.stream().map(employeesCalendarDetails -> {
                JobAndPriceDTO jobAndPriceDTO = new JobAndPriceDTO();
                jobAndPriceDTO.setJobId(employeesCalendarDetails.getJobId());
                jobAndPriceDTO.setPrice(employeesCalendarDetails.getPrice());
                jobAndPriceDTO.setCode(employeesCalendarDetails.getCode());
                return jobAndPriceDTO;
            }).collect(Collectors.toList());
            TimeSlotDTO timeSlotDTO = new TimeSlotDTO();
            timeSlotDTO.setTimeSlotStart(employeesCalendar.getTimeSlotStart());
            timeSlotDTO.setTimeSlotLength(employeesCalendar.getTimeSlotLength());
            timeSlotDTO.setJobAndPriceList(jobAndPriceDTOList);
            if (CommonUtils.isEmpty(employeesCalendar.getStander())){
                List<TimeSlotDTO> timeSlotDTOS = map3.getOrDefault("", new ArrayList<>());
                timeSlotDTOS.add(timeSlotDTO);
                sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                map3.put("", timeSlotDTOS);
            }else if (employeesCalendar.getStander() == false){ //日期
                List<TimeSlotDTO> timeSlotDTOS = map1.getOrDefault(employeesCalendar.getDate(), new ArrayList<>());
                timeSlotDTOS.add(timeSlotDTO);
                sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                map1.put(employeesCalendar.getDate(), timeSlotDTOS);
            }else if (employeesCalendar.getStander() == true){ //周
                String weekString = employeesCalendar.getWeek();
                for (int i = 0; i < weekString.length(); i++) {
                    Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                    List<TimeSlotDTO> timeSlotDTOS = map2.getOrDefault(weekInteger, new ArrayList<>());
                    timeSlotDTOS.add(timeSlotDTO);
                    sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                    map2.put(weekInteger, timeSlotDTOS);
                }
            }
        });

        Map<LocalDate, List<TimeSlotDTO>> calendarMap = new HashMap<>();
        LocalDate start = dateSlot.getStart();
        LocalDate end = dateSlot.getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            if (map1.containsKey(date)){
                //日期模板生效
                calendarMap.put(date, map1.get(date));
            }else if (!map2.isEmpty()){
                //周模板生效
                calendarMap.put(date, map2.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>()));
            }else if (!map3.isEmpty()){
                //通用模板生效
                calendarMap.put(date, map3.get(""));
            }
        }

        return calendarMap;
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
}
