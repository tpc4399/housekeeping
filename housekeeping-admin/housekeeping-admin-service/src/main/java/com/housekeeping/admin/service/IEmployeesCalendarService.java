package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.EmployeesCalendarDTO;
import com.housekeeping.admin.dto.EmployeesCalendarDateDTO;
import com.housekeeping.admin.dto.EmployeesCalendarWeekDTO;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/12 16:21
 */
public interface IEmployeesCalendarService extends IService<EmployeesCalendar> {
    R setCalendar(EmployeesCalendarDTO employeesCalendarDTO);
    R setCalendarWeek(EmployeesCalendarWeekDTO employeesCalendarWeekDTO);
    R setCalendarDate(EmployeesCalendarDateDTO employeesCalendarOneDateDTO);
}
