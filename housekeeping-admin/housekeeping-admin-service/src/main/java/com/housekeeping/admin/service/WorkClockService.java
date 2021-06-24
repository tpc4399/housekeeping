package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.SysJobNote;
import com.housekeeping.admin.entity.WorkClock;
import com.housekeeping.common.utils.R;

import java.util.List;


public interface WorkClockService extends IService<WorkClock> {

    R workStart(String phonePrefix,String phone);

    R workEnd(String phonePrefix,String phone);

    WorkClock getByWorkId(Integer id);
}
