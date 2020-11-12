package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesCalendarDTO;
import com.housekeeping.admin.dto.EmployeesCalendarDateDTO;
import com.housekeeping.admin.dto.EmployeesCalendarWeekDTO;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.admin.mapper.EmployeesCalendarMapper;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

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
            EmployeesCalendar employeesCalendar = new EmployeesCalendar();
            employeesCalendar.setEmployeesId(employeesCalendarDTO.getEmployeesId());
            employeesCalendar.setTimeSlotStart(timeSlotVo.getTimeSlotStart());
            employeesCalendar.setTimeSlotLength(timeSlotVo.getTimeSlotLength());
            baseMapper.insert(employeesCalendar);
        });
        return R.ok("更新成功");
    }

    @Override
    public R setCalendarWeek(EmployeesCalendarWeekDTO employeesCalendarWeekDTO) {
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
        return R.ok("更新成功");
    }

    @Override
    public R setCalendarDate(EmployeesCalendarDateDTO employeesCalendarDateDTO) {
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
        return R.ok("更新成功");
    }
}
