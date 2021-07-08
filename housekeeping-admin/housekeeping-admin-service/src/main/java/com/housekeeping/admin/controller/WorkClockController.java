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
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/workStart")
    public R workStart(@RequestParam Integer id,
                       @RequestParam String phone,
                       @RequestParam String phonePrefix){
        return workClockService.workStart(id,phonePrefix,phone);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】工作检查")
    @PostMapping("/workCheck")
    public R workStart(@RequestParam Integer id,
                       @RequestParam String staffCheck){
        return workClockService.workCheck(id,staffCheck);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】保潔員下班打卡")
    @PostMapping("/workEnd")
    public R workEnd(@RequestParam Integer id,
                     @RequestParam String phone,
                     @RequestParam String phonePrefix){
        return workClockService.workEnd(id,phonePrefix,phone);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【保洁员】【公司】【经理】保潔員上传图片")
    @PostMapping("/uploadPhoto")
    public R workEnd(@RequestParam("file") MultipartFile file,
                     @RequestParam Integer id,
                     @RequestParam Integer sort){
        return workClockService.uploadPhoto(file,id,sort);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【保洁员】【公司】【经理】保潔員總結")
    @PostMapping("/uploadPhotoAndSummary")
    public R workEnd(@RequestBody WorkClockDTO workClockDTO){
        return workClockService.uploadSummary(workClockDTO);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客戶】客戶确认")
    @PostMapping("/customerConfirm")
    public R workEnd(@RequestParam Integer id){
        return workClockService.customerConfirm(id);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客戶】客戶評價")
    @PostMapping("/customerEvaluation")
    public R workEnd(@RequestBody CustomerEvaluationDTO customerEvaluationDTO){
        return workClockService.customerEvaluation(customerEvaluationDTO);
    }
}
