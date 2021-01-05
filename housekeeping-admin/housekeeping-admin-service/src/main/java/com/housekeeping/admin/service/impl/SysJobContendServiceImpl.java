package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.mapper.SysJobContendMapper;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.admin.vo.SysJobContendVo;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("sysJobContendService")
public class SysJobContendServiceImpl extends ServiceImpl<SysJobContendMapper, SysJobContend> implements ISysJobContendService {
    @Override
    public R getTreeByIds(Integer[] ids) {
        List<SysJobContendVo> sysJobContendVoList = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            SysJobContendVo sysJobContendVo = new SysJobContendVo();

            QueryWrapper qw1 = new QueryWrapper();
            qw1.eq("id", ids[i]);
            SysJobContend sysJobContend1 = baseMapper.selectOne(qw1);
            sysJobContendVo.setContend(sysJobContend1.getContend());

            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("parent_id", ids[i]);
            List<SysJobContend> sysJobContend2 = baseMapper.selectList(qw2);
            List<String> sysJobContend22 = sysJobContend2.stream().map(x -> {
                return x.getContend();
            }).collect(Collectors.toList());
            sysJobContendVo.setContends(sysJobContend22);
        }
        return R.ok(sysJobContendVoList, "查找成功");
    }
}
