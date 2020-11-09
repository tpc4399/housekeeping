package com.housekeeping.admin.controller;


import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="員工controller",tags={"員工信息管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesDetails")
public class EmployeesDetailsController {

    private final EmployeesDetailsService employeesDetailsService;

    @ApiOperation("新增員工")
    @LogFlag(description = "新增員工")
    @GetMapping("/SaveEmp")
    public R saveEmp(@RequestBody EmployeesDetails employeesDetails){
        return employeesDetailsService.saveEmp(employeesDetails);
    }

}
