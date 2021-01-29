package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.EmployeesJobsDTO;
import com.housekeeping.admin.service.IEmployeesJobsService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2021/1/11 10:25
 */
@Api(tags = "【员工&工作】中间表接口")
@AllArgsConstructor
@RestController
@RequestMapping("/employeesJobs")
public class EmployeesJobsController {

    private final IEmployeesJobsService employeesJobsService;

//    @ApiOperation("【保洁员】修改员工工作内容")
//    @PutMapping("/updateEmployeesJobs")
//    public R updateEmployeesJobs(@RequestBody EmployeesJobsDTO employeesJobsDTO){
//        return employeesJobsService.updateEmployeesJobs(employeesJobsDTO);
//    }

}
