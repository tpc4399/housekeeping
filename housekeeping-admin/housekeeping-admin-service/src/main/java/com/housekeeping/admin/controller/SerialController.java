package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.service.ISerialService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2021/5/27 16:56
 */
@Api(tags={"【流水】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/serial")
public class SerialController {

    private final ISerialService serialService;

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理員】分頁查詢流水")
    @GetMapping("/pageOfSerial")
    public R pageOfSerial(Page page){
        return serialService.pageOfSerial(page);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理員】流水关联信息——照片以及描述")
    @GetMapping("/serialPhotos")
    public R serialPhotos(String serialNumber){
        return serialService.serialPhotos(serialNumber);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理員】流水关联信息——工作内容")
    @GetMapping("/serialWorks")
    public R serialWorks(String serialNumber){
        return serialService.serialWorks(serialNumber);
    }

}
