package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesCalendarDTO;
import com.housekeeping.admin.dto.EmployeesCalendarDateDTO;
import com.housekeeping.admin.dto.EmployeesCalendarWeekDTO;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.admin.mapper.EmployeesCalendarMapper;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.admin.vo.TimeSlotVo;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @create 2020/11/12 16:22
 */
@Service("employeesCalendarService")
public class EmployeesCalendarServiceImpl extends ServiceImpl<EmployeesCalendarMapper, EmployeesCalendar> implements IEmployeesCalendarService {

    @Override
    public R setCalendar(EmployeesCalendarDTO employeesCalendarDTO) {
        employeesCalendarDTO.getTimeSlots().forEach(timeSlotVo -> {
            /* 初始化 **/
            EmployeesCalendar employeesCalendar = new EmployeesCalendar();
            employeesCalendar.setEmployeesId(employeesCalendarDTO.getEmployeesId());
            employeesCalendar.setTimeSlotStart(timeSlotVo.getTimeSlotStart());
            employeesCalendar.setTimeSlotLength(timeSlotVo.getTimeSlotLength());
            /* 初始化 **/

            /* 删除原先 **/
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("employees_id", employeesCalendar.getEmployeesId());
            queryWrapper.eq("stander", "");
            baseMapper.delete(queryWrapper);
            /* 删除原先 **/

            /* 插入新设置的值 **/
            baseMapper.insert(employeesCalendar);
            /* 插入新设置的值 **/

        });
        return R.ok("更新成功");
    }

    @Override
    public R setCalendarWeek(EmployeesCalendarWeekDTO employeesCalendarWeekDTO) {

        /* 检查星期几重复性 **/
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("stander", 1);
        queryWrapper.eq("employees_id", employeesCalendarWeekDTO.getEmployeesId());
        List<EmployeesCalendar> exists = baseMapper.selectList(queryWrapper);
        List<Map> res = new ArrayList<>();
        exists.forEach(x -> {
            String existWeeks = x.getWeek();
            List<Integer> targetWeeks = employeesCalendarWeekDTO.getWeeks();
            List<TimeSlotVo> timeSlotVos = employeesCalendarWeekDTO.getTimeSlots();
            /** 先检查有没有重复的一天，再检查一天内有没有重复的时间段 */
            targetWeeks.forEach(y -> {
                if (existWeeks.contains(y.toString())){
                    PeriodOfTime periodOfTime1 = new PeriodOfTime();
                    periodOfTime1.setTimeSlotStart(x.getTimeSlotStart());
                    periodOfTime1.setTimeSlotLength(x.getTimeSlotLength());
                    timeSlotVos.forEach(z -> {
                        PeriodOfTime periodOfTime2 = new PeriodOfTime();
                        periodOfTime2.setTimeSlotStart(z.getTimeSlotStart());
                        periodOfTime2.setTimeSlotLength(z.getTimeSlotLength());
                        if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                            Map<String, Object> entity = new HashMap<>();
                            entity.put("week", y);
                            entity.put("exists", periodOfTime1);
                            entity.put("target", periodOfTime2);
                            res.add(entity);
                        }
                    });
                }
            });
        });
        /* 检查星期几重复性 **/

        /** 检查重复度 */
        if (res.size() == 0){
            //什么都不做
        }else {
            return R.failed(res, "設置失敗，時間段衝突");
        }
        /** 检查重复度 */

        /* 初始化和插入新的值 **/
        employeesCalendarWeekDTO.getTimeSlots().forEach(timeSlotVo -> {
            EmployeesCalendar employeesCalendar = new EmployeesCalendar();
            employeesCalendar.setEmployeesId(employeesCalendarWeekDTO.getEmployeesId());
            employeesCalendar.setStander(true);
            AtomicReference<String> week = new AtomicReference<>("");
            employeesCalendarWeekDTO.getWeeks().forEach(x -> {
                week.set(week.get() + x.toString());
            });
            employeesCalendar.setWeek(week.get());
            employeesCalendar.setTimeSlotStart(timeSlotVo.getTimeSlotStart());
            employeesCalendar.setTimeSlotLength(timeSlotVo.getTimeSlotLength());
            baseMapper.insert(employeesCalendar);
        });
        /* 初始化和插入新的值 **/

        return R.ok("設置成功");
    }

    @Override
    public R setCalendarDate(EmployeesCalendarDateDTO employeesCalendarDateDTO) {

        /* 检查日期的重复性 **/
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("stander", 0);
        queryWrapper.eq("employees_id", employeesCalendarDateDTO.getEmployeesId());
        List<EmployeesCalendar> exists = baseMapper.selectList(queryWrapper);
        List<Map> res = new ArrayList<>();
        exists.forEach(x -> {
            LocalDate existsDate = x.getData();
            employeesCalendarDateDTO.getDate().forEach(y -> {
                if (existsDate.equals(y)){
                    PeriodOfTime periodOfTime1 = new PeriodOfTime();
                    periodOfTime1.setTimeSlotStart(x.getTimeSlotStart());
                    periodOfTime1.setTimeSlotLength(x.getTimeSlotLength());
                    employeesCalendarDateDTO.getTimeSlots().forEach(z -> {
                        PeriodOfTime periodOfTime2 = new PeriodOfTime();
                        periodOfTime2.setTimeSlotStart(z.getTimeSlotStart());
                        periodOfTime2.setTimeSlotLength(z.getTimeSlotLength());
                        if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                            Map<String, Object> entity = new HashMap<>();
                            entity.put("date", y);
                            entity.put("exists", periodOfTime1);
                            entity.put("target", periodOfTime2);
                            res.add(entity);
                        }
                    });
                }
            });
        });

        /* 检查日期的重复性 **/

        /** 检查重复度 */
        if (res.size() == 0){
            //什么都不做
        }else {
            return R.failed(res, "設置失敗，時間段衝突");
        }
        /** 检查重复度 */

        /* 初始化和插入新的值 **/
        employeesCalendarDateDTO.getDate().forEach(x -> {
            employeesCalendarDateDTO.getTimeSlots().forEach(y -> {
                EmployeesCalendar employeesCalendar = new EmployeesCalendar();
                employeesCalendar.setEmployeesId(employeesCalendarDateDTO.getEmployeesId());
                employeesCalendar.setStander(false);
                employeesCalendar.setData(x);
                employeesCalendar.setTimeSlotStart(y.getTimeSlotStart());
                employeesCalendar.setTimeSlotLength(y.getTimeSlotLength());
                baseMapper.insert(employeesCalendar);
            });
        });
        /* 初始化和插入新的值 **/

        return R.ok("更新成功");
    }
}
