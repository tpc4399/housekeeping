package com.housekeeping.admin.controller;

import com.housekeeping.admin.entity.CompanyPriceAdjustment;
import com.housekeeping.admin.entity.EmployeesPriceAdjustment;
import com.housekeeping.admin.service.CompanyPriceAdjustmentService;
import com.housekeeping.admin.service.IEmployeesPriceAdjustmentService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(value="公司controller",tags={"【员工指定日期价格调整】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyPriceAdjustment")
public class CompanyPriceAdjustmentController {

    private final CompanyPriceAdjustmentService companyPriceAdjustmentService;


    @PostMapping
    @ApiOperation("新增指定日期段收费模板")
    public R add(@RequestBody CompanyPriceAdjustment companyPriceAdjustment){
        return companyPriceAdjustmentService.add(companyPriceAdjustment);
    }

    @PostMapping("/update")
    @ApiOperation("修改指定日期段收费模板")
    public R update(@RequestBody CompanyPriceAdjustment companyPriceAdjustment){
        return companyPriceAdjustmentService.cusUpdate(companyPriceAdjustment);
    }

    @DeleteMapping
    @ApiOperation("刪除指定日期段收费")
    public R delete(@RequestParam Integer id) {
        return R.ok(companyPriceAdjustmentService.removeById(id));
    }

    @ApiOperation("通過id、companyId查詢日期段收费")
    @GetMapping("/getAll")
    public R getAll(Integer id,Integer companyId){
        return companyPriceAdjustmentService.getAll(id,companyId);
    }


    @ApiOperation("员工根据模板新增节日价格调整")
    @GetMapping("/copyByEmp")
    public R copyByEmp(Integer id,Integer empId){
        return companyPriceAdjustmentService.copyByEmp(id, empId);
    }


}
