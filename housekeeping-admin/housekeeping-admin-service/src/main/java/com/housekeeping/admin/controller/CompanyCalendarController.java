package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.SetCompanyCalendarDTO;
import com.housekeeping.admin.dto.UpdateCompanyCalendarDTO;
import com.housekeeping.admin.service.ICompanyCalendarService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2021/5/31 8:58
 */
@Api(tags = "【公司时间表模板】接口")
@RestController
@AllArgsConstructor
@RequestMapping("/companyCalendar")
public class CompanyCalendarController {

    private final ICompanyCalendarService companyCalendarService;

    @Access(RolesEnum.USER_COMPANY)
    @PostMapping("/setCalendar")
    @ApiOperation("【公司】添加时间表模板，公司id可以不用传")
    public R setCalendar(@RequestBody SetCompanyCalendarDTO dto){
        return companyCalendarService.setCalendar(dto);
    }

    @Access(RolesEnum.USER_COMPANY)
    @PostMapping("/updateCalendar")
    @ApiOperation("【公司】修改时间表模板")
    public R updateCalendar(@RequestBody UpdateCompanyCalendarDTO dto){
        return companyCalendarService.updateCalendar(dto);
    }

    @Access(RolesEnum.USER_COMPANY)
    @GetMapping("/mineCalendar")
    @ApiOperation("【公司】查看自己时间表")
    public R mineCalendar(){
        return companyCalendarService.mineCalendar();
    }

    @Access(RolesEnum.USER_COMPANY)
    @DeleteMapping
    @ApiOperation("【公司】刪除某一條時間表")
    public R del(Integer id){
        return companyCalendarService.del(id);
    }

    @Access(RolesEnum.USER_COMPANY)
    @GetMapping
    @ApiOperation("【公司】根據id查詢公司排班記錄")
    public R getById(Integer id){
        return R.ok(companyCalendarService.getById(id), "獲取成功");
    }

}
