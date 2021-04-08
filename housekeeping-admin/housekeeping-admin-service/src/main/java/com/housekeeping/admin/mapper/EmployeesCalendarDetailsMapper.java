package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.EmployeesCalendarDetails;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/29 16:55
 */
public interface EmployeesCalendarDetailsMapper extends BaseMapper<EmployeesCalendarDetails> {
    List<EmployeesCalendarDetails> groupByCalendarIdHaving(List<Integer> calendarIds);
}
