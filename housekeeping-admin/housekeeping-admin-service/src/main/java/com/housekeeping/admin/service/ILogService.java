package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.LogDTO;
import com.housekeeping.admin.entity.Log;
import com.housekeeping.common.utils.R;

public interface ILogService extends IService<Log> {
    R addLog(Log log);
    R getAll(IPage page, LogDTO logDTO);
}