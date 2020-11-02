package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.LogDTO;
import com.housekeeping.admin.entity.Log;
import com.housekeeping.admin.service.ILogService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value="日志controller",tags={"日志管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final ILogService logService;

    @ApiOperation("【增】插入一条日志")
    @PostMapping("/addOne")
    public void addLog(Log log){
        logService.addLog(log);
    }

    @ApiOperation("【查】分页查询")
    @PostMapping("/page")
    public R getAll(IPage page, @Param("logDTO") LogDTO logDTO){
        return logService.getAll(page, logDTO);
    }
}
