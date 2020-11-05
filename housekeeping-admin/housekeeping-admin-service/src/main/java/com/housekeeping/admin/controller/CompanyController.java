package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.RegisterDTO;
import com.housekeeping.admin.entity.Company;
import com.housekeeping.admin.service.CompanyService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="公司controller",tags={"公司管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/company")
public class CompanyController {

   private final CompanyService companyService;

    @ApiOperation("新增公司")
    @LogFlag(description = "新增公司")
    @PostMapping("/saveCompany")
    public R saveCompany(@RequestBody Company company){
        boolean result = companyService.save(company);
        if(result){
            return R.ok("新增成功");
        }else {
            return R.failed("请求参数错误");
        }
    }

    @ApiOperation("新增公司")
    @LogFlag(description = "新增公司")
    @PostMapping("/updateCompany")
    public R updateCompany(@RequestBody Company company){
        boolean result = companyService.updateById(company);
        if(result){
            return R.ok("修改成功");
        }else {
            return R.failed("请求参数错误");
        }
    }
}
