package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.Index;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.mapper.IndexMapper;
import com.housekeeping.admin.service.IIndexService;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("indexService")
public class IndexServiceImpl extends ServiceImpl<IndexMapper, Index> implements IIndexService {

    @Resource
    private ISysJobContendService jobContendService;

    @Override
    public R getCusById(Integer id) {
        List<Integer> ids = baseMapper.getContentIds(id);
        ArrayList<SysJobContend> sysJobContends = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            SysJobContend byId = jobContendService.getById(ids.get(i));
            sysJobContends.add(byId);
        }
        return R.ok(sysJobContends);
    }
}
