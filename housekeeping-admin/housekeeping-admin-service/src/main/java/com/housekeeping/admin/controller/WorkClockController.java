package com.housekeeping.admin.controller;


import com.housekeeping.admin.dto.CustomerEvaluationDTO;
import com.housekeeping.admin.dto.WorkClockDTO;
import com.housekeeping.admin.service.WorkClockService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("/workStart")
    public R workStart(@RequestParam Integer id,
                       @RequestParam String phone,
                       @RequestParam String phonePrefix){
        return workClockService.workStart(id,phonePrefix,phone);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】保潔員下班打卡")
    @PutMapping("/workEnd")
    public R workEnd(@RequestParam Integer id,
                     @RequestParam String phone,
                     @RequestParam String phonePrefix){
        return workClockService.workEnd(id,phonePrefix,phone);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】保潔員上傳圖片及總結")
    @PutMapping("/uploadPhotoAndSummary")
    public R workEnd(@RequestBody WorkClockDTO workClockDTO){
        return workClockService.uploadPhotoAndSummary(workClockDTO);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客戶】客戶評價")
    @PutMapping("/customerEvaluation")
    public R workEnd(@RequestBody CustomerEvaluationDTO customerEvaluationDTO){
        return workClockService.customerEvaluation(customerEvaluationDTO);
    }
}
