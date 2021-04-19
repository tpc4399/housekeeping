package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.auth.annotation.PassToken;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
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

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】【經理】【公司】【平台】設置员工钟点工工作内容")
    @PutMapping("/setJobs")
    public R setJobs(@RequestBody SetEmployeesJobsDTO dto){
        return employeesCalendarService.setJobs(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】【經理】【公司】【平台】設置员工时间表 新接口，价格只与时段相关，需要先设置员工工作内容")
    @PutMapping("/setCalendar2")
    public R setCalendar2(@RequestBody SetEmployeesCalendar2DTO dto){
        return employeesCalendarService.setCalendar2(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】【經理】【公司】【平台】修改员工时间表")
    @PutMapping("/updateCalendar2")
    public R updateCalendar2(@RequestBody UpdateEmployeesCalendarDTO dto){
        return employeesCalendarService.updateCalendar2(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】【經理】【公司】【平台】修改员工时间表")
    @DeleteMapping("{id}")
    public R del(@PathVariable Integer id){
        return employeesCalendarService.del(id);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】【經理】【公司】【平台】設置员工时间表 通用模板：每日")
    @PutMapping("/setCalendar")
    public R setCalendar(@RequestBody SetEmployeesCalendarDTO dto){
        return employeesCalendarService.setCalendar(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】【經理】【公司】【平台】設置员工时间表 周模板添加內容")
    @PutMapping("/addCalendarWeek")
    public R addCalendarWeek(@RequestBody SetEmployeesCalendarWeekDTO dto){
        return employeesCalendarService.addCalendarWeek(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】【經理】【公司】【平台】設置员工时间表 按日期添加內容")
    @PutMapping("/setCalendarDate")
    public R setCalendarDate(@RequestBody SetEmployeesCalendarDateDTO dto){
        return employeesCalendarService.addCalendarDate(dto);
    }

    @ApiOperation("根据时间段和员工_id 獲取員工日期段內的時間表，就是员工设定的可工作时间，而非员工的空闲时间。以详细列举形式返回")
    @PostMapping("/getCalendarByDateSlot")
    public R getCalendarByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlotDTO>> res = employeesCalendarService.getCalendarByDateSlot(dto.getDateSlot(), dto.getId(), "");
        if (CommonUtils.isEmpty(res)){
            return R.failed("該員工沒有設置時間表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("根据时间段和员工_id 獲取員工日期段內的空闲时间。以详细列举形式返回")
    @PostMapping("/getFreeTimeByDateSlot")
    public R getFreeTimeByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlotDTO>> res = employeesCalendarService.getFreeTimeByDateSlot(dto.getDateSlot(), dto.getId(), "");
        if (CommonUtils.isEmpty(res)){
            return R.failed("該員工沒有設置時間表或没有闲置时间");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("根据时间段和员工_id 獲取員工日期段內的空闲时间。以详细列举形式返回2")
    @PostMapping("/getFreeTimeByDateSlot2")
    public R getFreeTimeByDateSlot2(@RequestBody GetCalendarByDateSlotDTO dto){
        List<FreeDateDTO> res = employeesCalendarService.getFreeTimeByDateSlot2(dto.getDateSlot(), dto.getId(), "");
        if (CommonUtils.isEmpty(res)){
            return R.failed("該員工沒有設置時間表或没有闲置时间");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("根据保洁员id获取保洁员技能标签")
    @GetMapping("/tags")
    public R getSkillTags(Integer employeesId){
        return employeesCalendarService.getSkillTags(employeesId);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】预约钟点工服务")
    @PostMapping("/makeAnAppointment")
    public R makeAnAppointment(@RequestBody MakeAnAppointmentDTO dto){
        return employeesCalendarService.makeAnAppointment(dto);
    }


    @Access(RolesEnum.USER_EMPLOYEES)
    @ApiOperation("【保洁员】获取自己的排班")
    @GetMapping("/getMyScheduling")
    public R getMyScheduling(){
        Integer userId = TokenUtils.getCurrentUserId();
        return employeesCalendarService.getSchedulingByUserId(userId);
    }

    @ApiOperation("获取保洁员的排班")
    @GetMapping("/getScheduling/{userId}")
    public R getScheduling(@PathVariable Integer userId){
        return employeesCalendarService.getSchedulingByUserId(userId);
    }

    @ApiOperation("获取保洁员的排班")
    @GetMapping("/getScheduling2/{employeesId}")
    public R getScheduling2(@PathVariable Integer employeesId){
        return employeesCalendarService.getSchedulingByEmployeesId(employeesId);
    }

}
