package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value="員工controller",tags={"員工信息管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesDetails")
public class EmployeesDetailsController {

    private final EmployeesDetailsService employeesDetailsService;

    @ApiOperation("新增員工")
    @LogFlag(description = "新增員工")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody EmployeesDetails employeesDetails){
        return employeesDetailsService.saveEmp(employeesDetails);
    }

    @ApiOperation("修改員工信息")
    @LogFlag(description = "修改員工信息")
    @PostMapping("/updateEmp")
    public R updateEmp(@RequestBody EmployeesDetails employeesDetails){
        return employeesDetailsService.updateEmp(employeesDetails);
    }

    @ApiOperation("刪除員工")
    @LogFlag(description = "刪除員工")
    @DeleteMapping("/deleteEmp")
    public R deleteEmp(@RequestBody EmployeesDetails employeesDetails){
        return R.ok(employeesDetailsService.removeById(employeesDetails));
    }

    @ApiOperation("查詢當前公司員工(所有、id)")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page")
    public R page(Page page,Integer id){
        return R.ok(employeesDetailsService.cusPage(page,id));
    }

    @ApiOperation("根据id生成登入链接")
    @GetMapping("/getLinkToLogin/{id}")
    public R getLinkToLogin(@PathVariable Integer id, @RequestParam("h") Long h, HttpServletRequest request){
        return employeesDetailsService.getLinkToLogin(id, h, request);
    }

}
