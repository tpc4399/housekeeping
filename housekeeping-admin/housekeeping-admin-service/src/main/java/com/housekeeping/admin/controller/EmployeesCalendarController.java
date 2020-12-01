package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.EmployeesCalendarDTO;
import com.housekeeping.admin.dto.EmployeesCalendarDateDTO;
import com.housekeeping.admin.dto.EmployeesCalendarWeekDTO;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/12 16:24
 */
@Api(value="员工日程表controller",tags={"【公司】员工日程表接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesCalendar")
public class EmployeesCalendarController {

    private final IEmployeesCalendarService employeesCalendarService;

    @ApiOperation("【员工】设置员工最通用的每日工作时间")
    @PutMapping("/setCalendar")
    public R setCalendar(@RequestBody EmployeesCalendarDTO employeesCalendarDTO){
        return employeesCalendarService.setCalendar(employeesCalendarDTO);
    }

    /***
     * 按周設置會默認刪除通用的工作時間
     * @param employeesCalendarWeekDTO
     * @return
     */
    @ApiOperation("【员工】按周几设置员工最每日工作时间")
    @PutMapping("/setCalendarWeek")
    public R setCalendarWeek(@RequestBody EmployeesCalendarWeekDTO employeesCalendarWeekDTO){
        return employeesCalendarService.setCalendarWeek(employeesCalendarWeekDTO);
    }

    @ApiOperation("【员工】按日期设置员工最每日工作时间")
    @PutMapping("/setCalendarDate")
    public R setCalendarDate(@RequestBody EmployeesCalendarDateDTO employeesCalendarDateDTO){
        return employeesCalendarService.setCalendarDate(employeesCalendarDateDTO);
    }

    @ApiOperation("【员工】获取员工日程表")
    @GetMapping("/getCalendar/{employeesId}")
    private R getCalendarByEmployees(@PathVariable("employeesId") Integer employeesId){
        return employeesCalendarService.getCalendarByEmployees(employeesId);
    }
}
