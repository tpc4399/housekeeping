package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.AddJobContendDTO;
import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.entity.SysJobNote;
import com.housekeeping.admin.mapper.SysJobContendMapper;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.admin.service.ISysJobNoteService;
import com.housekeeping.admin.vo.ContentVO;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("sysJobContendService")
public class SysJobContendServiceImpl extends ServiceImpl<SysJobContendMapper, SysJobContend> implements ISysJobContendService {

    @Resource
    private ISysJobNoteService sysJobNoteService;

    @Override
    public R add(AddJobContendDTO dos) {
        SysJobContend sysJobContend = new SysJobContend(dos.getId(), dos.getContend(),dos.getServicePlace(),dos.getArea(),dos.getHome());
        Integer maxIndexId;
        synchronized (this){
            this.save(sysJobContend);
            maxIndexId = ((SysIndex) CommonUtils.getMaxId("sys_job_content", this)).getId();
        }
        List<Integer> notes = dos.getNotes();
        notes.forEach(x ->{
            baseMapper.insertNote(maxIndexId,x);
        });
        return R.ok("添加成功");
    }

    @Override
    public R getAll(List<Integer> ids) {
        if (CommonUtils.isEmpty(ids)){
            return R.ok(this.list(), "查詢成功");
        }
        List<ContentVO> res = new ArrayList<ContentVO>();
        List<Integer> noExist = new ArrayList<>();
        ids.forEach(id -> {
            List<SysJobNote> sysJobNotes = new ArrayList<>();
            ContentVO contentVO = new ContentVO();
            SysJobContend sjc = this.getById(id);
            if (CommonUtils.isEmpty(sjc)){
                noExist.add(id);
                return;
            }
            List<Integer> noteIds = baseMapper.getAll(id);
            for (int i = 0; i < noteIds.size(); i++) {
                SysJobNote byId = sysJobNoteService.getById(noteIds.get(i));
                sysJobNotes.add(byId);
            }
            contentVO.setName(sjc.getContend());
            contentVO.setNotes(sysJobNotes);
            res.add(contentVO);
        });
        if (noExist.size() != 0){
            return R.failed(noExist, "該id不存在");
        }
        return R.ok(res, "查詢成功");
    }

    @Override
    public R getAll2(String ids) {
        List<ContentVO> res = new ArrayList<ContentVO>();
        String[] idStr = ids.split(" ");
        List<Integer> noExist = new ArrayList<>();
        for (int i = 0; i < idStr.length; i++) {

            List<SysJobNote> sysJobNotes = new ArrayList<>();
            ContentVO contentVO = new ContentVO();

            Integer id = Integer.valueOf(idStr[i]);
            SysJobContend sjc = this.getById(id);
            if (CommonUtils.isEmpty(sjc)){
                noExist.add(id);
                continue;
            }
            List<Integer> noteIds = baseMapper.getAll(id);
            for (int x = 0; x < noteIds.size(); x++) {
                SysJobNote byId = sysJobNoteService.getById(noteIds.get(x));
                sysJobNotes.add(byId);
            }
            contentVO.setName(sjc.getContend());
            contentVO.setNotes(sysJobNotes);
            res.add(contentVO);
        }
        if (noExist.size() != 0){
            return R.failed(noExist, "該id不存在");
        }
        return R.ok(res, "查詢成功");
    }

    @Override
    public R cusRemove(List<Integer> ids) {
        ids.forEach(x ->{
            baseMapper.cusRemoveNote(x);
            baseMapper.cusRemoveJob(x);
            this.removeById(x);
        });
        return R.ok("删除成功");
    }

    @Override
    public R cusUpdate(AddJobContendDTO sysJobContend) {
        SysJobContend sysJobContend1 = new SysJobContend(sysJobContend.getId(), sysJobContend.getContend(), sysJobContend.getServicePlace(),sysJobContend.getArea(),sysJobContend.getHome());
        this.updateById(sysJobContend1);
        baseMapper.cusRemoveNote(sysJobContend.getId());
        List<Integer> notes = sysJobContend.getNotes();
        notes.forEach(x ->{
            baseMapper.insertNote(sysJobContend.getId(), x);
        });
        return R.ok("修改成功");
    }

}
