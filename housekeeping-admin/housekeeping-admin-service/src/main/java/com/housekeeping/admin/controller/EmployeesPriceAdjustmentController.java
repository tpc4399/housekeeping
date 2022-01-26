package com.housekeeping.admin.controller;

import com.housekeeping.admin.entity.EmployeesPriceAdjustment;
import com.housekeeping.admin.service.IEmployeesPriceAdjustmentService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(value="员工controller",tags={"【员工指定日期价格调整】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesPriceAdjustment")
public class EmployeesPriceAdjustmentController {

    private final IEmployeesPriceAdjustmentService employeesPriceAdjustmentService;


    @PostMapping
    @ApiOperation("新增指定日期段收费")
    public R add(@RequestBody EmployeesPriceAdjustment employeesPriceAdjustment){
        return employeesPriceAdjustmentService.add(employeesPriceAdjustment);
    }

    @PostMapping("/update")
    @ApiOperation("修改日期段收费")
    public R update(@RequestBody EmployeesPriceAdjustment employeesPriceAdjustment){
        return employeesPriceAdjustmentService.cusUpdate(employeesPriceAdjustment);
    }

    @DeleteMapping
    @ApiOperation("刪除指定日期段收费")
    public R delete(@RequestParam Integer id) {
        return R.ok(employeesPriceAdjustmentService.removeById(id));
    }

    @ApiOperation("通過id、empId查詢日期段收费")
    @GetMapping("/getAll")
    public R getAll(Integer id,Integer empId){
        return employeesPriceAdjustmentService.getAll(id,empId);
    }



}
