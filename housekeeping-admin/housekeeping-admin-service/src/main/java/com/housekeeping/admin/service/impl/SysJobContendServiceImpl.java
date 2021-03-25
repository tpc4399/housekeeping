package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.AddJobContendDTO;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.mapper.SysJobContendMapper;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("sysJobContendService")
public class SysJobContendServiceImpl extends ServiceImpl<SysJobContendMapper, SysJobContend> implements ISysJobContendService {

    @Override
    public R add(List<AddJobContendDTO> dos) {
        List<SysJobContend> sysJobContendList = new ArrayList<>();
        dos.forEach(dto -> {
            sysJobContendList.add(new SysJobContend(dto.getId(), dto.getContend()));
        });
        this.saveBatch(sysJobContendList);
        return R.ok("添加成功");
    }

    @Override
    public R getAll(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)){
            return R.ok(this.list(), "查詢成功");
        }
        List<String> res = new ArrayList<>();
        List<Integer> noExist = new ArrayList<>();
        ids.forEach(id -> {
            SysJobContend sjc = this.getById(id);
            if (CommonUtils.isEmpty(sjc)){
                noExist.add(id);
                return;
            }
            res.add(sjc.getContend());
        });
        if (noExist.size() != 0){
            return R.failed(noExist, "該id不存在");
        }
        return R.ok(res, "查詢成功");
    }

    @Override
    public R getAll2(String ids) {
        List<String> res = new ArrayList<>();
        String[] idStr = ids.split(" ");
        List<Integer> noExist = new ArrayList<>();
        for (int i = 0; i < idStr.length; i++) {
            Integer id = Integer.valueOf(idStr[i]);
            SysJobContend sjc = this.getById(id);
            if (CommonUtils.isEmpty(sjc)){
                noExist.add(id);
                continue;
            }
            res.add(sjc.getContend());
        }
        if (noExist.size() != 0){
            return R.failed(noExist, "該id不存在");
        }
        return R.ok(res, "查詢成功");
    }

}
