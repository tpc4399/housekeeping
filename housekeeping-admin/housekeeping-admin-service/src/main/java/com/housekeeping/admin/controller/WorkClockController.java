package com.housekeeping.admin.controller;


import com.housekeeping.admin.dto.CustomerEvaluationDTO;
import com.housekeeping.admin.dto.WorkClockDTO;
import com.housekeeping.admin.entity.WorkClock;
import com.housekeeping.admin.service.WorkClockService;
import com.housekeeping.admin.vo.UploadPhotoVO;
import com.housekeeping.admin.vo.WorkCheckVO;
import com.housekeeping.admin.vo.WorkClockTick;
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

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【个体户】保潔員上班打卡")
    @PostMapping("/workStart")
    public R workStart(@RequestParam Integer id,
                       @RequestParam String phone,
                       @RequestParam String phonePrefix){
        return workClockService.workStart(id,phonePrefix,phone);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【个体户】保洁员图片打勾")
    @PostMapping("/tick")
    public R workStart(@RequestBody WorkClockTick workClock){
        WorkClock byId = workClockService.getById(workClock.getId());
        byId.setPhoto1Status(workClock.getPhoto1Status());
        byId.setPhoto2Status(workClock.getPhoto2Status());
        byId.setPhoto3Status(workClock.getPhoto3Status());
        byId.setPhoto4Status(workClock.getPhoto4Status());
        byId.setPhoto5Status(workClock.getPhoto5Status());
        return R.ok(workClockService.updateById(byId));
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【个体户】工作检查")
    @PostMapping("/workCheck")
    public R workStart(@RequestBody WorkCheckVO workCheckVO){
        return workClockService.workCheck(workCheckVO);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【个体户】保潔員下班打卡")
    @PostMapping("/workEnd")
    public R workEnd(@RequestParam Integer id,
                     @RequestParam String phone,
                     @RequestParam String phonePrefix){
        return workClockService.workEnd(id,phonePrefix,phone);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【公司】【经理】【个体户】保潔員上传图片")
    @PostMapping("/uploadPhoto")
    public R workEnd(@RequestParam("file") MultipartFile file,
                     @RequestParam Integer id,
                     @RequestParam Integer sort){
        return workClockService.uploadPhoto(file,id,sort);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【公司】【经理】【个体户】保潔員總結")
    @PostMapping("/uploadPhotoAndSummary")
    public R workEnd(@RequestBody WorkClockDTO workClockDTO){
        return workClockService.uploadSummary(workClockDTO);
    }

    @Access({RolesEnum.USER_EMPLOYEES,RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER,RolesEnum.USER_PERSONAL})
    @ApiOperation("【保洁员】【公司】【经理】【个体户】保潔員上传工作图片")
    @PostMapping("/uploadPhotos")
    public R uploadPhoto(@RequestBody UploadPhotoVO uploadPhotoVO){
        return workClockService.uploadPhotos(uploadPhotoVO);
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
