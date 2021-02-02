package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IEmployeesJobsService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}
