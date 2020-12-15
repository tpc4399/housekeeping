package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IEmployeesWorkExperienceService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2020/12/15 14:26
 */
@Api(tags={"【工作经验】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesWorkExperience")
public class EmployeesWorkExperienceController {

    private final IEmployeesWorkExperienceService employeesWorkExperienceService;

    @ApiOperation("获取某員工工作经验")
    @GetMapping
    public R getAll(Integer employeesId){
        return employeesWorkExperienceService.getAll(employeesId);
    }

}
