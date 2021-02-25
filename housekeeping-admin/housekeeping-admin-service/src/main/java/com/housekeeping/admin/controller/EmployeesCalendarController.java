package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @create 2020/11/12 16:24
 */
@Api(tags={"【员工日程表】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesCalendar")
public class EmployeesCalendarController {

    private final IEmployeesCalendarService employeesCalendarService;

    @ApiOperation("【员工】【經理】【公司】【平台】設置员工时间表 通用模板：每日")
    @PutMapping("/setCalendar")
    public R setCalendar(@RequestBody SetEmployeesCalendarDTO dto){
        return employeesCalendarService.setCalendar(dto);
    }

    @ApiOperation("【员工】【經理】【公司】【平台】設置员工时间表 周模板添加內容")
    @PutMapping("/addCalendarWeek")
    public R addCalendarWeek(@RequestBody SetEmployeesCalendarWeekDTO dto){
        return employeesCalendarService.addCalendarWeek(dto);
    }

    @ApiOperation("【员工】【經理】【公司】【平台】設置员工时间表 按日期添加內容")
    @PutMapping("/setCalendarDate")
    public R setCalendarDate(@RequestBody SetEmployeesCalendarDateDTO dto){
        return employeesCalendarService.addCalendarDate(dto);
    }

    @ApiOperation("【管理员】【公司】【经理】【员工】【客户】根据时间段和员工_id 獲取員工日期段內的時間表，就是员工设定的可工作时间，而非员工的空闲时间。以详细列举形式返回")
    @PostMapping("/getCalendarByDateSlot")
    public R getCalendarByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlotDTO>> res = employeesCalendarService.getCalendarByDateSlot(dto.getDateSlot(), dto.getId(), "");
        if (CommonUtils.isEmpty(res)){
            return R.failed("該員工沒有設置時間表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("【管理员】【公司】【经理】【员工】【客户】根据时间段和员工_id 獲取員工日期段內的空闲时间。以详细列举形式返回")
    @PostMapping("/getFreeTimeByDateSlot")
    public R getFreeTimeByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlotDTO>> res = employeesCalendarService.getFreeTimeByDateSlot(dto.getDateSlot(), dto.getId(), "");
        if (CommonUtils.isEmpty(res)){
            return R.failed("該員工沒有設置時間表或没有闲置时间");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

}
