package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.CompanyCalendar;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.admin.mapper.CompanyCalendarMapper;
import com.housekeeping.admin.service.ICompanyCalendarService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author su
 * @create 2021/5/31 8:56
 */
@Service("companyCalendarService")
public class CompanyCalendarServiceImpl
        extends ServiceImpl<CompanyCalendarMapper, CompanyCalendar>
        implements ICompanyCalendarService {

    @Resource
    private ICompanyDetailsService companyDetailsService;

    @Override
    public R setCalendar(SetCompanyCalendarDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentD(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }

        /* 公司id查询 */
        Integer companyId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());

        /* 添加新的 */
        StringBuilder week = new StringBuilder();
        dto.getWeek().forEach(wk->{
            week.append(wk);
        });
        dto.getTimeSlotPriceDTOList().forEach(timeSlot -> {
            CompanyCalendar companyCalendar =
                    new CompanyCalendar(
                            companyId,
                            true,
                            null,
                            week.toString(),
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            companyCalendar.setHourlyWage(new BigDecimal(timeSlot.getPrice()));
            companyCalendar.setCode(timeSlot.getCode());
            companyCalendar.setType(timeSlot.getType());
            companyCalendar.setPercentage(timeSlot.getPercentage());
            baseMapper.insert(companyCalendar);
        });

        return R.ok(null,"設置成功");
    }

    @Override
    public R updateCalendar(UpdateCompanyCalendarDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentE(dto);
        if (!res.isEmpty()) return R.failed(res, "數據不合理");

        /* 修改 */
        CompanyCalendar cc = this.getById(dto.getId());
        StringBuilder week = new StringBuilder();
        dto.getWeeks().forEach(wk->{
            week.append(wk);
        });
        cc.setWeek(week.toString());
        cc.setTimeSlotStart(dto.getTimeSlotStart());
        cc.setTimeSlotLength(dto.getTimeSlotLength());
        cc.setType(dto.getType());
        cc.setPercentage(dto.getPercentage());
        cc.setHourlyWage(new BigDecimal(dto.getPrice()));
        cc.setCode(dto.getCode());
        this.updateById(cc);
        return R.ok(null, "修改成功");
    }

    @Override
    public List<EmployeesCalendar> initEmpCalendar(Integer companyId, Integer employeesId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("company_id", companyId);
        List<CompanyCalendar> companyCalendarList = this.list(qw);
        List<EmployeesCalendar> calendars = companyCalendarList.stream().map(cc -> {
            EmployeesCalendar ec = new EmployeesCalendar(
                    employeesId,
                    cc.getStander(),
                    cc.getDate(),
                    cc.getWeek(),
                    cc.getTimeSlotStart(),
                    cc.getTimeSlotLength()
            );
            ec.setHourlyWage(cc.getHourlyWage());
            cc.setCode(ec.getCode());
            return ec;
        }).collect(Collectors.toList());
        return calendars;
    }

    @Override
    public R mineCalendar() {
        Integer companyUserId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(companyUserId);
        QueryWrapper qw = new QueryWrapper();
        qw.eq("company_id", companyId);
        List<CompanyCalendar> companyCalendarList = this.list(qw);
        if (companyCalendarList == null) return R.failed(null, "該公司未設置時間表");
        if (companyCalendarList.size() == 0) return R.failed(null, "該公司未設置時間表");
        return R.ok(companyCalendarList, "獲取成功");
    }

    @Override
    public List<CompanyCalendar> getCalendar(Integer companyId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("company_id", companyId);
        List<CompanyCalendar> companyCalendarList = this.list(qw);
        if (companyCalendarList == null){
            return null;
        }
        if (companyCalendarList.size() == 0) {
        return null;
        }
        return companyCalendarList;
    }

    @Override
    public R del(Integer id) {
        CompanyCalendar cc = this.getById(id);
        if (cc == null) return R.failed(null, "該記錄不存在，無需刪除");
        this.removeById(id);
        return R.ok(null, "刪除成功");
    }

    /* 用于添加 時間段合理性判斷：周   假設都不為空 */
    public List<String> rationalityJudgmentD(SetCompanyCalendarDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        Map<Integer, List<PeriodOfTime>> map = new HashMap<>();
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("company_id", dto.getCompanyId());
        qw.eq("stander", true);
        List<CompanyCalendar> companyCalendars = this.list(qw);
        companyCalendars.forEach(calendar -> {
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

    /* 用于修改 時間段合理性判斷：周   假設都不為空 */
    public List<String> rationalityJudgmentE(UpdateCompanyCalendarDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        Map<Integer, List<PeriodOfTime>> map = new HashMap<>();
        CompanyCalendar cc = this.getById(dto.getId());
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("company_id", cc.getCompanyId());
        qw.eq("stander", true);
        List<CompanyCalendar> companyCalendarList = this.list(qw);
        companyCalendarList.forEach(calendar -> {
            if (calendar.getId().equals(cc.getId())) return;   //把当前dto要暂时删掉
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
}
