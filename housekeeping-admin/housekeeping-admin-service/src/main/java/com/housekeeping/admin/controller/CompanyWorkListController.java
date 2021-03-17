package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.CompanyWorkListDTO;
import com.housekeeping.admin.dto.CompanyWorkListQueryDTO;
import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/18 16:12
 */
@Api(value="公司controller",tags={"【感興趣】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companyWorkList")
public class CompanyWorkListController {

    private final ICompanyWorkListService companyWorkListService;

    @Access(RolesEnum.USER_COMPANY)
    @ApiOperation("【公司】感興趣操作接口")
    @GetMapping("/{demandOrderId}")
    public R beInterested(@PathVariable Integer demandOrderId){
        return R.ok();
    }

}
