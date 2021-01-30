package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2020/11/12 16:21
 */
public interface IEmployeesCalendarService extends IService<EmployeesCalendar> {
    /* 設置通用規則 */
    R setCalendar(SetEmployeesCalendarDTO dto);
    /* 添加一條周規則 */
    R addCalendarWeek(SetEmployeesCalendarWeekDTO dto);
    /* 添加一條日規則 */
    R addCalendarDate(SetEmployeesCalendarDateDTO dto);
}
