package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.AddJobContendDTO;
import com.housekeeping.admin.dto.JobNoteDTO;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.entity.SysJobNote;
import com.housekeeping.admin.mapper.SysJobContendMapper;
import com.housekeeping.admin.mapper.SysJobNoteMapper;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.admin.service.ISysJobNoteService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("sysJobNoteService")
public class SysJobNoteServiceImpl extends ServiceImpl<SysJobNoteMapper, SysJobNote> implements ISysJobNoteService {

    @Override
    public R add(List<SysJobNote> dos) {
        List<SysJobNote> sysJobNotes = new ArrayList<>();
        dos.forEach(dto -> {
            sysJobNotes.add(new SysJobNote(dto.getId(), dto.getNote()));
        });
        this.saveBatch(sysJobNotes);
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
            SysJobNote sjc = this.getById(id);
            if (CommonUtils.isEmpty(sjc)){
                noExist.add(id);
                return;
            }
            res.add(sjc.getNote());
        });
        if (noExist.size() != 0){
            return R.failed(noExist, "該id不存在");
        }
        return R.ok(res, "查詢成功");
    }

    @Override
    public R cusRemove(List<Integer> ids) {
        ids.forEach(id -> {
            this.removeById(id);
            baseMapper.cusRemove(id);
        });
        return R.ok("删除成功!");
    }

    @Override
    public R getAllByContent(Integer contentId) {
        List<Integer> noteIds = baseMapper.getAllNoteByContent(contentId);
        List<SysJobNote> collect = noteIds.stream().map(x -> {
            SysJobNote byId = this.getById(x);
            return byId;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

}
