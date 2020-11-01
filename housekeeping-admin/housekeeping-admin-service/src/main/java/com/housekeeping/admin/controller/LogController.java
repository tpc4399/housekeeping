package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.LogDTO;
import com.housekeeping.admin.entity.Log;
import com.housekeeping.admin.service.ILogService;
import com.housekeeping.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final ILogService logService;

    @PostMapping("/addOne")
    public void addLog(Log log){
        logService.addLog(log);
    }

    @PostMapping("/page")
    public R getAll(IPage page, LogDTO logDTO){
        return logService.getAll(page, logDTO);
    }
}
