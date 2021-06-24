package com.housekeeping.admin.controller;


import com.housekeeping.admin.service.WorkClockService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2020/12/11 16:09
 */
@Api(tags={"【工作打卡】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/workClock")
public class WorkClockController {

    private final WorkClockService workClockService;

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】保潔員上班打卡")
    @PutMapping("workStart")
    public R workStart(@RequestParam String phonePrefix,
                       @RequestParam String phone){
        return workClockService.workStart(phonePrefix,phone);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】保潔員下班打卡")
    @PutMapping("workEnd")
    public R workEnd(@RequestParam String phonePrefix,
                     @RequestParam String phone){
        return workClockService.workEnd(phonePrefix,phone);
    }

}
