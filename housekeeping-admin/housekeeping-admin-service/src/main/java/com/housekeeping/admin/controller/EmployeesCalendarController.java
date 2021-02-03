package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
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

    @ApiOperation("獲取員工日期段內的時間表")
    @PostMapping("/getCalendarByDateSlot")
    public R getCalendarByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlotDTO>> res = employeesCalendarService.getCalendarByDateSlot(dto.getDateSlot(), dto.getEmployeesId());
        if (CommonUtils.isEmpty(res)){
            return R.failed("該員工沒有設置時間表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

}
