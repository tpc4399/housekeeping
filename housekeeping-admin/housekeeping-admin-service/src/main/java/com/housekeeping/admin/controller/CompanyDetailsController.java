package com.housekeeping.admin.controller;

import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="公司controller",tags={"公司管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyDetails")
public class CompanyDetailsController {

   private final ICompanyDetailsService companyDetailsService;

    @ApiOperation("修改公司信息")
    @LogFlag(description = "修改公司信息")
    @PostMapping("/update")
    public R updateCompany(@RequestBody CompanyDetails companyDetails){
        boolean result = companyDetailsService.updateById(companyDetails);
        if(result){
            return R.ok("修改成功");
        }else {
            return R.failed("请求参数错误");
        }
    }

    @ApiOperation("公司上传logo")
    @LogFlag(description = "公司上传logo")
    @GetMapping("/uploadLogo")
    public R uploadLogo(){
        return R.ok();
    }
}
