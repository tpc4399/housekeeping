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

    @ApiOperation("獲取員工日期段內的包工時間表")
    @PostMapping("/getCalendarByDateSlot")
    public R getCalendarByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlot>> res = employeesContractService.getCalendarByContractId(dto.getDateSlot(), dto.getId());
        if (CommonUtils.isEmpty(res)){
            return R.failed("該包工沒有設置時間表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("獲取員工日期段內的包工時間表的闲置时间表")
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
