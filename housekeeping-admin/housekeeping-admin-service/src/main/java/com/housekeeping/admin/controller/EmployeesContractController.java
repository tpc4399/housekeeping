package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.service.IEmployeesContractService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}
