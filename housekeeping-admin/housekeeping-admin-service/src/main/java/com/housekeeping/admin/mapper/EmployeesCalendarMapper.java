package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.admin.entity.SysJobContend;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 员工日程表Mapper
 * @Author su
 * @create 2020/11/12 16:19
 */
public interface EmployeesCalendarMapper extends BaseMapper<EmployeesCalendar> {

    List<SysJobContend> getSkillTags(@Param("employeesId") Integer employeesId);

}
