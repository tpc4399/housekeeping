package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.service.IEmployeesContractService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation("【员工】添加一个自己的包工服务")
    @PostMapping("/add")
    public R add(@RequestBody AddEmployeesContractDTO dto){
        return employeesContractService.add(dto);
    }

}
