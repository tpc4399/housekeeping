package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.CompanyWorkListDTO;
import com.housekeeping.admin.dto.CompanyWorkListQueryDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("分頁查詢工作列表")
    @GetMapping("/pageOfWorkList")
    public R page(IPage<SysOrder> page, CompanyWorkListQueryDTO companyWorkListQueryDTO){
        return R.ok();
    }

}
