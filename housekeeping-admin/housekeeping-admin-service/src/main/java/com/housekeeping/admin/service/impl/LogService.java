package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.LogDTO;
import com.housekeeping.admin.entity.Log;
import com.housekeeping.admin.mapper.LogMapper;
import com.housekeeping.admin.service.ILogService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

@Service("/logService")
public class LogService extends ServiceImpl<LogMapper, Log> implements ILogService {

    @Override
    public R addLog(Log log) {
        baseMapper.insert(log);
        return R.ok("添加日志成功");
    }

    @Override
    public R getAll(IPage page, LogDTO logDTO) {
        QueryWrapper qr = new QueryWrapper();
        if (CommonUtils.isNotEmpty(logDTO.getTitle())){
            qr.like("title", logDTO.getTitle());
        }
        if (CommonUtils.isNotEmpty(logDTO.getLastReviserId())){
            qr.eq("last_reviser_id", logDTO.getLastReviserId());
        }
        if (CommonUtils.isNotEmpty(logDTO.getRemoteAddr())){
            qr.like("remote_addr", logDTO.getRemoteAddr());
        }
        if (CommonUtils.isNotEmpty(logDTO.getUserAgent())){
            qr.like("user_agent", logDTO.getUserAgent());
        }
        if (CommonUtils.isNotEmpty(logDTO.getRequestUri())){
            qr.like("request_uri", logDTO.getRequestUri());
        }
        if (CommonUtils.isNotEmpty(logDTO.getMethod())){
            qr.eq("method", logDTO.getMethod());
        }
        if (CommonUtils.isNotEmpty(logDTO.getParams())){
            qr.eq("params", logDTO.getParams());
        }
        if (CommonUtils.isNotEmpty(logDTO.getTime())){
            qr.eq("time", logDTO.getTime());
        }
        if (CommonUtils.isNotEmpty(logDTO.getCreateTime1())
                && CommonUtils.isNotEmpty(logDTO.getCreateTime2())){
            qr.ge("create_time", logDTO.getCreateTime1()); //ge  >=
            qr.lt("create_time", logDTO.getCreateTime2()); //lt  <
        }
        IPage<Log> re = baseMapper.selectPage(page, qr);
        return R.ok(re, "分页查询日志成功");
    }

}
