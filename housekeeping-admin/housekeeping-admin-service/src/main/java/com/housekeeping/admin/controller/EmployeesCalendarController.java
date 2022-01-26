package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.admin.vo.setSchedulingVO;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【员工】【經理】【公司】【平台】【個體戶】設置员工钟点工工作内容")
    @PutMapping("/setJobs")
    public R setJobs(@RequestBody SetEmployeesJobsDTO dto){
        return employeesCalendarService.setJobs(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【员工】【經理】【公司】【個體戶】【平台】設置员工时间表 新接口，价格只与时段相关，需要先设置员工工作内容")
    @PutMapping("/setCalendar2")
    public R setCalendar2(@RequestBody SetEmployeesCalendar2DTO dto){
        return employeesCalendarService.setCalendar2(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【员工】【經理】【公司】【平台】【個體戶】修改员工时间表")
    @PutMapping("/updateCalendar2")
    public R updateCalendar2(@RequestBody UpdateEmployeesCalendarDTO dto){
        return employeesCalendarService.updateCalendar2(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【员工】【經理】【公司】【平台】【個體戶】删除员工某条时间表")
    @DeleteMapping("{id}")
    public R del(@PathVariable Integer id){
        return employeesCalendarService.del(id);
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

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】确认订单")
    @PostMapping("/confirmOrder")
    public R confirmOrder(@RequestBody MakeAnAppointmentDTO dto){
        return employeesCalendarService.confirmOrder(dto);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】指定日期预约服务")
    @PostMapping("/makeAnAppointmentByDate")
    public R makeAnAppointment(@RequestBody MakeAnAppointmentByDateDTO dto){
        return employeesCalendarService.MakeAnAppointmentByDateDTO(dto);
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】指定日期确认订单")
    @PostMapping("/confirmOrderByDate")
    public R confirmOrder(@RequestBody MakeAnAppointmentByDateDTO dto){
        return employeesCalendarService.confirmOrderByDate(dto);
    }




    @ApiOperation("根据id获取排班信息")
    @GetMapping("/getById")
    public R getById(@RequestParam Integer id){
        return employeesCalendarService.getByCalendarId(id);
    }


    @ApiOperation("根据时间段和员工_id 獲取員工日期段內的空闲时间。以详细列举形式返回2")
    @PostMapping("/getFreeTimeByDateSlot2")
    public R getFreeTimeByDateSlot2(@RequestBody GetCalendarByDateSlotDTO dto){
        List<FreeDateTimeDTO> res = employeesCalendarService.getFreeTimeByDateSlot4(dto);
        if (CommonUtils.isEmpty(res)){
            return R.failed("該員工沒有設置時間表或没有闲置时间");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("根据年份月份和员工_id 獲取員工日期段內的空闲时间。以详细列举形式返回")
    @PostMapping("/getFreeTimeByMonth")
    public R getFreeTimeByMonth(@RequestBody GetFreeTimeByMonthDTO dto){
        return employeesCalendarService.getFreeTimeByMonth(dto);
    }

    @ApiOperation("（新）根据年份月份和员工_id 獲取員工日期段內的空闲时间。以详细列举形式返回2")
    @PostMapping("/getFreeTimeByMonth2")
    public R getFreeTimeByMonth2(@RequestBody GetFreeTimePriceByMonthDTO dto){
        return employeesCalendarService.getFreeTimeByMonth2(dto);
    }


    @ApiOperation("根据年份月份和员工_id 获取无时间日期")
    @PostMapping("/getAbsenceDaysByMonth")
    public R getAbsenceDaysByMonth(@RequestBody GetFreeTimeByMonthDTO dto){
        return employeesCalendarService.getAbsenceDaysByMonth(dto);
    }

    @ApiOperation("根据日期段 获取无时间日期")
    @PostMapping("/getAbsenceDaysByDateSlot")
    public R getAbsenceDaysByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        return employeesCalendarService.getAbsenceDaysByDateSlot(dto);
    }

    @Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】查询本公司所有员工的时间表")
    @PostMapping("/getAllInCompany")
    public R getAllInCompany(){
        return employeesCalendarService.getAllInCompany();
    }

    @ApiOperation("获取保洁员的排班")
    @GetMapping("/getScheduling2/{employeesId}")
    public R getScheduling2(@PathVariable Integer employeesId){
        return employeesCalendarService.getSchedulingByEmployeesId(employeesId);
    }


    @ApiOperation("根據公司id獲取排班")
    @GetMapping("/getSchedulingById")
    public R getSchedulingById(@RequestParam Integer companyId){
        return employeesCalendarService.getSchedulingById(companyId);
    }

    @ApiOperation("批量新增員工排版")
    @PostMapping("/setSchedulingByIds")
    public R setSchedulingByIds(@RequestBody setSchedulingVO vo){
        return employeesCalendarService.setSchedulingByIds(vo);
    }
}
