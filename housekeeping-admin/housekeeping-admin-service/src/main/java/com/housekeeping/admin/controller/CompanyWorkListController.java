package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.CompanyWorkListDTO;
import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/11/18 16:12
 */
@Api(value="公司controller",tags={"【公司】工作列表接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyWorkList")
public class CompanyWorkListController {

    private final ICompanyWorkListService companyWorkListService;

    @ApiOperation("添加訂單到工作列表")
    @PostMapping
    public R addToTheWorkList(@RequestBody CompanyWorkListDTO companyWorkListDTO){
        return companyWorkListService.addToTheWorkList(companyWorkListDTO);
    }

}
