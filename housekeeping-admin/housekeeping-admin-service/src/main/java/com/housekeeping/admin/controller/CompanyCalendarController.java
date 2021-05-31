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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiOperation("【公司】添加时间表模板")
    public R setCalendar(@RequestBody SetCompanyCalendarDTO dto){
        return companyCalendarService.setCalendar(dto);
    }

    @Access(RolesEnum.USER_COMPANY)
    @PostMapping("/updateCalendar")
    @ApiOperation("【公司】修改时间表模板")
    public R updateCalendar(@RequestBody UpdateCompanyCalendarDTO dto){
        return companyCalendarService.updateCalendar(dto);
    }

}
