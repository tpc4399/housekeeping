package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.EmployeesCalendarDetails;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/29 16:56
 */
public interface IEmployeesCalendarDetailsService extends IService<EmployeesCalendarDetails> {

    List<EmployeesCalendarDetails> groupByCalendarIdHaving(List<Integer> calendarIds);

}
