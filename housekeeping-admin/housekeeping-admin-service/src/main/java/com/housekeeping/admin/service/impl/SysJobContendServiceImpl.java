package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.mapper.SysJobContendMapper;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("sysJobContendService")
public class SysJobContendServiceImpl
        extends ServiceImpl<SysJobContendMapper, SysJobContend>
        implements ISysJobContendService {
    @Override
    public R add(String contend) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("contend", contend);
        SysJobContend exist = baseMapper.selectOne(queryWrapper);
        if (CommonUtils.isNotEmpty(exist)){
            return R.failed("工作標籤"+ contend +"已存在，添加失敗");
        }
        SysJobContend sysJobContend = new SysJobContend();
        sysJobContend.setContend(contend);
        sysJobContend.setCreateTime(LocalDateTime.now());
        sysJobContend.setUpdateTime(LocalDateTime.now());
        sysJobContend.setLastReviserId(TokenUtils.getCurrentUserId());
        baseMapper.insert(sysJobContend);
        return R.ok("添加工作標籤"+ contend +"成功");
    }
}
