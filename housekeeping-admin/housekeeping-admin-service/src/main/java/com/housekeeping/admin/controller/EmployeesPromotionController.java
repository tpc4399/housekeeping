package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICompanyPromotionService;
import com.housekeeping.admin.service.IEmployeesPromotionService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags={"【員工推廣】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesPromotion")
public class EmployeesPromotionController {

    private final IEmployeesPromotionService employeesPromotionService;

    @ApiOperation("【公司】查詢當前公司所有員工推廣狀態")
    @GetMapping("/getEmpInfoByCompanyId")
    public R getEmpInfoByCompanyId(Integer empId,String empName){
        return employeesPromotionService.getEmpInfoByCompanyId(empId,empName);
    }

    @ApiOperation("【公司】推廣員工一天")
    @GetMapping("/promotionDay")
    public R promotionDay(Integer empId){
        return employeesPromotionService.promotionDay(empId);
    }

    @ApiOperation("【公司】推廣員工十天")
    @GetMapping("/promotionTenDay")
    public R promotionTenDay(Integer empId){
        return employeesPromotionService.promotionTenDay(empId);
    }

    @ApiOperation("【公司】從推廣員工中隨機取")
    @GetMapping("/getCompanyByRan")
    public R getEmpByRan(Integer random){
        return employeesPromotionService.getEmpByRan(random);
    }

    @ApiOperation("【管理员】查询所有推广中员工")
    @GetMapping("/getAllProEmp")
    public R getAllProEmp(){
        return employeesPromotionService.getAllProEmp();
    }
}
