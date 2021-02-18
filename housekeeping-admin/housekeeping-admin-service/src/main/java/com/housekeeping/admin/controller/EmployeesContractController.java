package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.dto.GetCalendarByDateSlotDTO;
import com.housekeeping.admin.dto.TimeSlotDTO;
import com.housekeeping.admin.service.IEmployeesContractService;
import com.housekeeping.admin.vo.TimeSlot;
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
 * @Date 2021/2/1 10:06
 */
@Api(tags={"【包工服务】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesContract")
public class EmployeesContractController {

    private final IEmployeesContractService employeesContractService;

    @ApiOperation("給員工添加一个包工服务")
    @PostMapping("/add")
    public R add(@RequestBody AddEmployeesContractDTO dto){
        return employeesContractService.add(dto);
    }

    @ApiOperation("获取员工的所有包工服务")
    @GetMapping("{employeesId}")
    public R getByEmployeesId(@PathVariable Integer employeesId){
        return employeesContractService.getByEmployeesId(employeesId);
    }

    @ApiOperation("【管理员】获取所有包工服务")
    @GetMapping
    public R getAll(){
        return R.ok(employeesContractService.list());
    }

    @ApiOperation("【管理员】【公司】【经理】【员工】【客户】根据时间段和包工_id 獲取包工時間表，就是这个包工的服务时间的一个详细列举")
    @PostMapping("/getCalendarByDateSlot")
    public R getCalendarByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlot>> res = employeesContractService.getCalendarByContractId(dto.getDateSlot(), dto.getId());
        if (CommonUtils.isEmpty(res)){
            return R.failed("該包工沒有設置時間表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("【管理员】【公司】【经理】【员工】【客户】根据时间段和包工_id 獲取員工包工時間表的闲置时间表，就是这段时间内这个员工可以做这个包工的时间。以详细列举形式返回")
    @PostMapping("/getFreeTimeByDateSlot")
    public R getFreeTimeByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlot>> res = employeesContractService.getFreeTimeByContractId(dto.getDateSlot(), dto.getId());
        if (CommonUtils.isEmpty(res)){
            return R.failed("該包工沒有闲置时间或包工没设置时间表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

}
